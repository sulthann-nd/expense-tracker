package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DailyLineChart(
    values: List<Double>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (values.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No data",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Box
        }

        val transitionValues = remember { Animatable(0f) }
        LaunchedEffect(values) { transitionValues.animateTo(1f) }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val maxV = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0).toFloat()
            val minV = (values.minOrNull() ?: 0.0).toFloat()
            val range = (maxV - minV).coerceAtLeast(1f)

            val stepX = w / (values.size - 1).coerceAtLeast(1)

            val points = values.mapIndexed { index, v ->
                val x = index * stepX
                val normalized = (v.toFloat() - minV) / range
                val y = h - (normalized * (h - 20f)) - 10f
                Offset(x, y)
            }

            // Area Fill
            val fillPath = Path().apply {
                moveTo(points.first().x, h)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, h)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Blue.copy(alpha = 0.18f), Color.Transparent)
                )
            )

            // Line
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = Color.Blue,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Dots
            points.forEach { center ->
                drawCircle(Color.White, radius = 4.dp.toPx(), center = center)
                drawCircle(Color.Blue, radius = 4.dp.toPx(), center = center, style = Stroke(width = 2.dp.toPx()))
            }
        }
    }
}