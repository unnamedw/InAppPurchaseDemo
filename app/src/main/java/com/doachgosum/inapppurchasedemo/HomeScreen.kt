package com.doachgosum.inapppurchasedemo

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.android.billingclient.api.BillingFlowParams

const val homeRoute = "home"
@Composable
fun HomeScreen(
    mainNavController: NavHostController,
    mainViewModel: MainViewModel,
    clientWrapper: BillingClientWrapper
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    lifecycle.removeObserver(clientWrapper)
    lifecycle.addObserver(clientWrapper)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val activity = LocalContext.current as Activity
        val products by mainViewModel.productList.collectAsState()
        val buttonModels by remember {
            derivedStateOf {
                products.map {
                    listOf(
                        ButtonModel(it.name) {
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
                        ButtonModel("consume ${it.name}") {
                            mainViewModel.consumeProduct(it) { purchase ->
                                clientWrapper.consumeOneTimeProduct(purchase)
                            }
                        }
                    )
                }.flatten()
            }
        }

        ButtonGroup(
            buttonModels = buttonModels
        )

        ButtonGroup(
            buttonModels = listOf(

            )
        )
    }
}

