package com.doachgosum.inapppurchasedemo.domain

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