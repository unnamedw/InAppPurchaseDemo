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
    val priceMicros: Long = 0L, // ex) ₩ 1,000 -> 1000000000
    val currencyCode: String = "",
)

data class BillingProductPurchase(
    val purchaseToken: String = "",
    val timestamp: Instant = Clock.System.now(),
)