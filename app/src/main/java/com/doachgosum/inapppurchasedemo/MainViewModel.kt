package com.doachgosum.inapppurchasedemo

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val clientWrapper: BillingClientWrapper
): ViewModel() {

    val productList: StateFlow<List<ProductDetails>> = clientWrapper.oneTimeProductWithProductDetails
    val purchases: StateFlow<List<Purchase>> = clientWrapper.oneTimeProductPurchases

//    fun buyProduct(activity: Activity, productDetails: ProductDetails) {
//        val billingParams = listOf(
//            BillingFlowParams.ProductDetailsParams.newBuilder()
//                .setProductDetails(productDetails)
//                .build()
//        )
//        clientWrapper.launchBillingFlow(
//            activity,
//            BillingFlowParams.newBuilder()
//                .setProductDetailsParamsList(billingParams)
//                .build()
//        )
//    }

    fun consumeProduct(productDetails: ProductDetails, onReadyToConsume: suspend (Purchase) -> Unit) = viewModelScope.launch {
        val purchases = purchases.value.firstOrNull { it.products.contains(productDetails.productId) } ?: return@launch

        onReadyToConsume(purchases)
    }
}