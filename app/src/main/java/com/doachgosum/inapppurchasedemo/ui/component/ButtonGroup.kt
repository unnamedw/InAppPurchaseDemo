package com.doachgosum.inapppurchasedemo.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val buttonHeight = 60.dp
private val buttonWidth = 240.dp
private val spaceDp = 16.dp

@Composable
private fun CenteredSurfaceColumn(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}

@Composable
fun ButtonGroup(
    buttonModels: List<ButtonModel>,
) {
    CenteredSurfaceColumn {
        for (buttonModel in buttonModels) {
            Button(
                modifier = Modifier.size(
                    width = buttonWidth,
                    height = buttonHeight
                ),
                onClick = buttonModel.onClick
            ) {
                Text(text = buttonModel.text)
            }
            Spacer(modifier = Modifier.height(spaceDp))
        }
    }
}

data class ButtonModel(val text: String, val onClick: () -> Unit)
