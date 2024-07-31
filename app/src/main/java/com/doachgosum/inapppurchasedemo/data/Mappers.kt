package com.doachgosum.inapppurchasedemo.data

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Domain models
 * */
data class BillingProductDetail(
    val productId: String,
    val productName: String,
    val formattedPrice: String = "",
    val price: Long = 0L,
    val localizedCurrency: String = "",
    val timestamp: Instant = Clock.System.now(),
)

data class BillingProductPurchase(
    val purchaseToken: String = ""
)

/**
 * Mappers
 * */
internal fun ProductDetails.toBillingProductDetail(): BillingProductDetail {
    return BillingProductDetail(
        productId = this.productId,
        productName = this.name,
    )
}

internal fun List<ProductDetails>.toBillingProductDetails(): List<BillingProductDetail> {
    return map {
        BillingProductDetail(
            productId = it.productId,
            productName = it.name
        )
    }
}

internal fun Purchase.toBillingProductPurchase(): BillingProductPurchase {
    return BillingProductPurchase(
        purchaseToken = this.purchaseToken
    )
}

internal fun List<Purchase>.toBillingProductPurchases(): List<BillingProductPurchase> {
    return map {
        BillingProductPurchase(
            purchaseToken = it.purchaseToken
        )
    }
}