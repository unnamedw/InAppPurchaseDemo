package com.doachgosum.inapppurchasedemo.data

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.doachgosum.inapppurchasedemo.domain.BillingProductDetail
import com.doachgosum.inapppurchasedemo.domain.BillingProductPurchase

/**
 * Mappers
 * */
internal fun ProductDetails.toBillingProductDetail(): BillingProductDetail {
    return BillingProductDetail(
        productId = this.productId,
        productName = this.name,
        formattedPrice = this.oneTimePurchaseOfferDetails?.formattedPrice ?: "",
        priceMicros = this.oneTimePurchaseOfferDetails?.priceAmountMicros ?: 0L,
        currencyCode = this.oneTimePurchaseOfferDetails?.priceCurrencyCode ?: ""
    )
}

internal fun List<ProductDetails>.toBillingProductDetails(): List<BillingProductDetail> {
    return map {
        it.toBillingProductDetail()
    }
}

internal fun Purchase.toBillingProductPurchase(): BillingProductPurchase {
    return BillingProductPurchase(
        purchaseToken = this.purchaseToken
    )
}

internal fun List<Purchase>.toBillingProductPurchases(): List<BillingProductPurchase> {
    return map {
        it.toBillingProductPurchase()
    }
}