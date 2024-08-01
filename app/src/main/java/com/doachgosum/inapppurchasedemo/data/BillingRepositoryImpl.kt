package com.doachgosum.inapppurchasedemo.data

import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper
import com.doachgosum.inapppurchasedemo.di.DiConstant
import com.doachgosum.inapppurchasedemo.di.DispatcherQualifiers
import com.doachgosum.inapppurchasedemo.domain.BillingProductDetail
import com.doachgosum.inapppurchasedemo.domain.BillingProductPurchase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class BillingRepositoryImpl @Inject constructor(
    @DispatcherQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DispatcherQualifiers.DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @Named(DiConstant.Named.PACKAGE_NAME) private val packageName: String,
    private val billingService: BillingService,
    private val billingClientWrapper: BillingClientWrapper,
    private val externalScope: CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher),
): BillingRepository {

    override val oneTimeProductDetails: StateFlow<List<BillingProductDetail>> =
        billingClientWrapper.oneTimeProductDetails
            .map { it.toBillingProductDetails() }
            .stateIn(externalScope, SharingStarted.WhileSubscribed(), emptyList())

    override val oneTimeProductPurchase: StateFlow<List<BillingProductPurchase>> =
        billingClientWrapper.oneTimeProductPurchases
            .map { it.toBillingProductPurchases() }
            .stateIn(externalScope, SharingStarted.WhileSubscribed(), emptyList())

    override suspend fun registerOneTimeProduct(productId: String, purchaseToken: String) = withContext(ioDispatcher) {
        billingService.registerProduct(
            packageName = packageName,
            productId = productId,
            purchaseToken = purchaseToken
        )
    }
}

interface BillingRepository {
    val oneTimeProductDetails: StateFlow<List<BillingProductDetail>>
    val oneTimeProductPurchase: StateFlow<List<BillingProductPurchase>>

    /**
     * 구매 후 서버 쪽에 등록하기 위한 함수
     * */
    suspend fun registerOneTimeProduct(
        productId: String,
        purchaseToken: String
    )
}



