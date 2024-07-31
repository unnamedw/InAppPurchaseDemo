package com.doachgosum.inapppurchasedemo.data

interface BillingService {

    suspend fun registerProduct(
        packageName: String,
        productId: String,
        purchaseToken: String
    )

}