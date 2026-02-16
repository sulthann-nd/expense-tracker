package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LegendDot(color: Color) {
    Box(
        modifier = Modifier.run {
            size(10.dp)
                .shadow(1.dp, CircleShape)
                .background(color, CircleShape)
                .border(1.dp, Color.White, CircleShape)
        }
    )
}