package com.doachgosum.inapppurchasedemo.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.doachgosum.inapppurchasedemo.MainViewModel
import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper

const val subscriptionRoute = "subscription"

@Composable
fun SubscriptionScreen(
    mainViewModel: MainViewModel,
    mainNavController: NavController,
    clientWrapper: BillingClientWrapper
) {
//    val subscriptionProducts = mainViewModel.
}