package com.doachgosum.inapppurchasedemo.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.doachgosum.inapppurchasedemo.utils.BillingConstants.LIST_OF_ONE_TIME_PRODUCTS
import com.doachgosum.inapppurchasedemo.di.DispatcherQualifiers
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BillingClientWrapper @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @DispatcherQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
) : DefaultLifecycleObserver, BillingClientStateListener,
    PurchasesUpdatedListener, ProductDetailsResponseListener, PurchasesResponseListener
{
    private fun logger(block: () -> String) {
        Log.d("BillingClient", block())
    }

    private val _oneTimeProductPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val oneTimeProductPurchases = _oneTimeProductPurchases.asStateFlow()

    private val _oneTimeProductDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val oneTimeProductDetails = _oneTimeProductDetails.asStateFlow()

    private val _purchaseEvent = MutableSharedFlow<List<Purchase>>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val purchaseEvent = _purchaseEvent.asSharedFlow()

    private var cachedPurchasesList: List<Purchase>? = null

    private lateinit var billingClient: BillingClient
    private var isInitialized = false

    fun startBillingConnection() {
        if (isInitialized) {
            terminateBillingConnection()
        }

        billingClient = BillingClient.newBuilder(applicationContext)
            .setListener(this)
            .enablePendingPurchases(
                // Not used for subscriptions.
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            ).build()

        isInitialized = true

        if (!billingClient.isReady) {
            logger { "billing client connected" }
            billingClient.startConnection(this)
        }
    }

    fun terminateBillingConnection() {
        if (billingClient.isReady) {
            logger { "billing client end" }
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            logger { "Billing response OK" }

            queryOneTimeProductPurchases()
            queryOneTimeProductDetails()
        } else {
            logger { billingResult.debugMessage }
        }
    }

    override fun onBillingServiceDisconnected() {
        logger { "Billing connection disconnected" }
        billingClient.startConnection(this)
    }

    fun queryOneTimeProductPurchases() {
        if (!billingClient.isReady) {
            logger { "queryPurchases: BillingClient is not ready" }
        }

        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            this
        )
    }

    fun queryOneTimeProductDetails(productIds: List<String> = LIST_OF_ONE_TIME_PRODUCTS) {
        val params = QueryProductDetailsParams.newBuilder()
        val productList = productIds.map { product ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(product)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        params.setProductList(productList).let {
            billingClient.queryProductDetailsAsync(it.build(), this)
        }
    }

    /**
     * 상품 목록을 조회했을 때 받는 콜백
     * */
    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>
    ) {
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        when {
            response.isOk -> {
                processProductDetails(productDetailsList)
            }
            response.isTerribleFailure -> {
                logger { "onProductDetailsResponse - Unexpected error: ${response.code} $debugMessage" }
            }
            else -> {
                logger { "onProductDetailsResponse: ${response.code} $debugMessage" }
            }
        }
    }

    private fun processProductDetails(productDetailsList: MutableList<ProductDetails>) {
        if (productDetailsList.isEmpty()) {
            postProductDetails(emptyList())
        } else {
            postProductDetails(productDetailsList)
        }
    }

    private fun postProductDetails(productDetailsList: List<ProductDetails>) {
        val oneTimeProductDetailsList  = productDetailsList
            .filter { it.productType == BillingClient.ProductType.INAPP }

        _oneTimeProductDetails.value = oneTimeProductDetailsList
    }

    /**
     * 구매 후 결과값이 들어오는 콜백
     * */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        logger { "onPurchasesUpdated: $responseCode $debugMessage" }
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    logger { "onPurchasesUpdated: null purchase list" }
                    processPurchases(null)
                } else {
                    _purchaseEvent.tryEmit(purchases)
                    processPurchases(purchases)
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                logger { "onPurchasesUpdated: User canceled the purchase" }
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                logger { "onPurchasesUpdated: The user already owns this item" }
            }

            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                logger { "onPurchasesUpdated: Developer error" }
            }

        }
    }

    /**
     * 구매내역 조회 시 받는 콜백
     * */
    override fun onQueryPurchasesResponse(billingResult: BillingResult, purchaseList: MutableList<Purchase>) {
        processPurchases(purchaseList)
    }

    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>): Boolean {
        val isUnchanged = purchasesList == cachedPurchasesList
        if (!isUnchanged) {
            cachedPurchasesList = purchasesList
        }
        return isUnchanged
    }

    private fun processPurchases(purchasesList: List<Purchase>?) {
        logger { "processPurchases: ${purchasesList?.size} purchase(s)" }
        purchasesList?.let { list ->
            if (isUnchangedPurchaseList(list)) {
                logger { "processPurchases: Purchase list has not changed" }
                return
            }
            externalScope.launch {
                val oneTimeProductPurchaseList = list.filter { purchase ->
                    LIST_OF_ONE_TIME_PRODUCTS.containsAll(purchase.products)
                }

                _oneTimeProductPurchases.emit(oneTimeProductPurchaseList)
            }
        }
    }

    /**
     * 구매 시작
     * */
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams, onPurchase: (BillingResult) -> Unit = {}) {
        if (!billingClient.isReady) {
            logger { "launchBillingFlow: BillingClient is not ready" }
        }

        val result = billingClient.launchBillingFlow(activity, params)
        onPurchase(result)
    }

    /**
     * For testing
     * */
    suspend fun consumeOneTimeProduct(purchase: Purchase): ConsumeResult {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        val consumeResult = withContext(ioDispatcher) {
            billingClient.consumePurchase(consumeParams)
        }

        return consumeResult
    }

    suspend fun acknowledgeOneTimeProduct(purchase: Purchase): BillingResult {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val acknowledgePurchaseResult = withContext(ioDispatcher) {
            billingClient.acknowledgePurchase(acknowledgePurchaseParams)
        }

        return acknowledgePurchaseResult
    }

//    companion object {
//
//        @Volatile
//        private var INSTANCE: BillingClientWrapper? = null
//
//        fun getInstance(applicationContext: Context): BillingClientWrapper =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: BillingClientWrapper(applicationContext).also { INSTANCE = it }
//            }
//    }

}

object BillingConstants {

    // Product IDs
    const val ONE_TIME_PRODUCT_1 = "testotp1"
    const val ONE_TIME_PRODUCT_2 = "testotp2"

    val LIST_OF_ONE_TIME_PRODUCTS = listOf(ONE_TIME_PRODUCT_1, ONE_TIME_PRODUCT_2)
}

@JvmInline
private value class BillingResponse(val code: Int) {
    val isOk: Boolean
        get() = code == BillingClient.BillingResponseCode.OK
    val canFailGracefully: Boolean
        get() = code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
    val isRecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ERROR,
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
        )
    val isNonrecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
        )
    val isTerribleFailure: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED,
            BillingClient.BillingResponseCode.USER_CANCELED,
        )
}