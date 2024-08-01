package com.doachgosum.inapppurchasedemo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseDetail(
    val productDetailOriginJson: String,
    val purchaseOriginJson: String
)

@Composable
fun PurchaseDetailScreen(
    billingClientWrapper: BillingClientWrapper,
    purchaseDetail: PurchaseDetail
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SelectionContainer(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text =
                    """
                        <상품정보>
                        ${purchaseDetail.productDetailOriginJson}
                        
                        <구매정보>
                        ${purchaseDetail.purchaseOriginJson}
                    """.trimIndent()
            )
        }

    }
}