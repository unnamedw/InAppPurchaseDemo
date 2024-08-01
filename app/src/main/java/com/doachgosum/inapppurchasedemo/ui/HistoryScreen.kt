package com.doachgosum.inapppurchasedemo.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.billingclient.api.Purchase.PurchaseState
import com.doachgosum.inapppurchasedemo.MainViewModel
import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper
import com.google.gson.Gson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


const val historyRoute = "history"

@Composable
fun HistoryScreen(
    mainNavController: NavController,
    mainViewModel: MainViewModel,
    clientWrapper: BillingClientWrapper
) {
    val history by clientWrapper.oneTimeProductPurchases.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    SelectionContainer(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                """
                                    구매상품 id: ${it.products},
                                    상태: ${it.purchaseState.toPurchaseStateString()},
                                    시간: ${it.purchaseTime.toFormattedTimestamp()},
                                    수량: ${it.quantity},
                                    Acknowledged: ${it.isAcknowledged}
                                """.trimIndent()
                        )
                    }

                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = {
                            mainViewModel.getProductsFromPurchases(purchase = it) { productDetails ->
                                mainNavController.navigate(
                                    PurchaseDetail(
                                        productDetailOriginJson = productDetails.map { it.toString() }.toString(),
                                        purchaseOriginJson = it.originalJson
                                    )
                                )
                            }
                        }
                    ) {
                        Text(text = "Detail")
                    }
                }
            }
        }
    }
}

fun Int.toPurchaseStateString(): String = when (this) {
    PurchaseState.PURCHASED -> "Purchased"
    PurchaseState.PENDING -> "Pending"
    PurchaseState.UNSPECIFIED_STATE -> "Unspecified"
    else -> "Unknown"
}

@SuppressLint("NewApi")
fun Long.toFormattedTimestamp(): String {
    val instant = Instant.ofEpochMilli(this)
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime: ZonedDateTime = instant.atZone(zoneId)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    return zonedDateTime.format(formatter)
}