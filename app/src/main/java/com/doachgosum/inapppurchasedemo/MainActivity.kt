package com.doachgosum.inapppurchasedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.doachgosum.inapppurchasedemo.ui.HistoryScreen
import com.doachgosum.inapppurchasedemo.ui.HomeScreen
import com.doachgosum.inapppurchasedemo.ui.OneTimeProductScreen
import com.doachgosum.inapppurchasedemo.ui.PurchaseDetail
import com.doachgosum.inapppurchasedemo.ui.PurchaseDetailScreen
import com.doachgosum.inapppurchasedemo.ui.SubscriptionScreen
import com.doachgosum.inapppurchasedemo.ui.historyRoute
import com.doachgosum.inapppurchasedemo.ui.homeRoute
import com.doachgosum.inapppurchasedemo.ui.oneTimeProductRoute
import com.doachgosum.inapppurchasedemo.ui.subscriptionRoute
import com.doachgosum.inapppurchasedemo.ui.theme.InAppPurchaseDemoTheme
import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var billingClientWrapper: BillingClientWrapper
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val mainNavController = rememberNavController()
            InAppPurchaseDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            modifier = Modifier
                                .fillMaxSize(),
                            navController = mainNavController,
                            startDestination = homeRoute,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {

                            composable(homeRoute) {
                                HomeScreen(
                                    mainNavController,
                                    mainViewModel,
                                    billingClientWrapper
                                )
                            }

                            composable(historyRoute) {
                                HistoryScreen(
                                    mainNavController = mainNavController,
                                    mainViewModel = mainViewModel,
                                    clientWrapper = billingClientWrapper
                                )
                            }

                            composable<PurchaseDetail> {
                                PurchaseDetailScreen(
                                    billingClientWrapper = billingClientWrapper,
                                    purchaseDetail = it.toRoute()
                                )
                            }

                            composable(oneTimeProductRoute) {
                                OneTimeProductScreen(
                                    mainNavController = mainNavController,
                                    mainViewModel = mainViewModel,
                                    clientWrapper = billingClientWrapper
                                )
                            }

                            composable(subscriptionRoute) {
                                SubscriptionScreen(
                                    mainViewModel = mainViewModel,
                                    mainNavController = mainNavController,
                                    clientWrapper = billingClientWrapper
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}