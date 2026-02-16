package com.example.expensetracker.ui.components

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class CategorySlice(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val percent: Float, // Using Float for Compose math
    val color: Color
)