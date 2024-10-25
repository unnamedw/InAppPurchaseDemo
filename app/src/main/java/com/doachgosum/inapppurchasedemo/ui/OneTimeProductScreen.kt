package com.doachgosum.inapppurchasedemo.ui

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.billingclient.api.BillingFlowParams
import com.doachgosum.inapppurchasedemo.MainViewModel
import com.doachgosum.inapppurchasedemo.ui.component.ButtonGroup
import com.doachgosum.inapppurchasedemo.ui.component.ButtonModel
import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

const val oneTimeProductRoute = "oneTimeProduct"

@Composable
fun OneTimeProductScreen(
    mainNavController: NavHostController,
    mainViewModel: MainViewModel,
    clientWrapper: BillingClientWrapper
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        mainViewModel.purchaseEvent.collectLatest {
            Log.d("MyTest", it.map { it.products }.toString())
            Toast.makeText(context, it.map { purchase -> purchase.products }.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        clientWrapper.startBillingConnection()
        onDispose {
            clientWrapper.terminateBillingConnection()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val activity = LocalContext.current as Activity
        val products by mainViewModel.productList.collectAsState()
        val buttonModels by remember {
            derivedStateOf {
                products.map {
                    listOfNotNull(

                        // 상품
                        ButtonModel("${it.name} (${it.oneTimePurchaseOfferDetails?.priceCurrencyCode} ${it.oneTimePurchaseOfferDetails?.formattedPrice})") {
                            val billingParams = listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(it)
                                    .build()
                            )
                            clientWrapper.launchBillingFlow(
                                activity,
                                BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(billingParams)
                                    .build()
                            )
                        },

                        // consume
                        ButtonModel("consume") {
                            mainViewModel.getProductPurchase(it) { purchase ->
                                val result = clientWrapper.consumeOneTimeProduct(purchase)
                                Toast.makeText(context, result.billingResult.responseCode.toResponseCodeString(), Toast.LENGTH_SHORT).show()
                            }
                        },

                        // acknowledge
                        ButtonModel("acknowledge") {
                            mainViewModel.getProductPurchase(it) { purchase ->
                                val result = clientWrapper.acknowledgeOneTimeProduct(purchase)
                                Toast.makeText(context, result.responseCode.toResponseCodeString(), Toast.LENGTH_SHORT).show()
                            }
                        }

                    )
                }.flatten()
            }
        }

        ButtonGroup(
            buttonModels = buttonModels
        )
    }
}