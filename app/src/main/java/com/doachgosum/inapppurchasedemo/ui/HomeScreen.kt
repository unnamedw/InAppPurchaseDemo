package com.doachgosum.inapppurchasedemo.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingFlowParams
import com.doachgosum.inapppurchasedemo.ui.component.ButtonGroup
import com.doachgosum.inapppurchasedemo.ui.component.ButtonModel
import com.doachgosum.inapppurchasedemo.MainViewModel
import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper

const val homeRoute = "home"
@Composable
fun HomeScreen(
    mainNavController: NavHostController,
    mainViewModel: MainViewModel,
    clientWrapper: BillingClientWrapper
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val activity = LocalContext.current as Activity
        val buttonModels = listOf(
            ButtonModel("One Time Product") {
                mainNavController.navigate(oneTimeProductRoute)
            },
            ButtonModel("Subscription") {
                mainNavController.navigate(subscriptionRoute)
            }
        )

        val purchaseHistoryButtonModel = listOf(ButtonModel("구매내역") { mainNavController.navigate(historyRoute) })
        ButtonGroup(
            buttonModels = buttonModels + purchaseHistoryButtonModel
        )
    }
}

fun Int.toResponseCodeString(): String = when (this) {
    BillingResponseCode.OK -> "OK"
    BillingResponseCode.FEATURE_NOT_SUPPORTED -> "FEATURE_NOT_SUPPORTED"
    BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
    BillingResponseCode.SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
    BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
    BillingResponseCode.SERVICE_TIMEOUT -> "SERVICE_TIMEOUT"
    BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
    BillingResponseCode.ERROR -> "ERROR"
    BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
    BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
    BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
    BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
    else -> "UNKNOWN"
}
