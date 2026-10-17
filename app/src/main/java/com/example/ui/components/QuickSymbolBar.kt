package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IdeBorder
import com.example.ui.theme.IdeSurfaceVariant
import com.example.ui.theme.TokenKeyword

@Composable
fun QuickSymbolBar(
    onSymbolClick: (String) -> Unit,
    onFormatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val symbols = listOf("{", "}", "(", ")", ";", "->", "=", ".", "\"", "val", "fun", "class", "import")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_symbol_bar"),
        color = IdeSurfaceVariant,
        tonalElevation = 4.dp,
        border = BorderStroke(0.5.dp, IdeBorder)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick Format Button
            Surface(
                onClick = onFormatClick,
                shape = RoundedCornerShape(6.dp),
                color = TokenKeyword.copy(alpha = 0.2f),
                modifier = Modifier
                    .padding(end = 6.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Text(
                    text = "FORMAT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TokenKeyword,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }

            // Quick Symbols
            symbols.forEach { symbol ->
                Surface(
                    onClick = { onSymbolClick(symbol) },
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Text(
                        text = symbol,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
