package com.example.expensetracker.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPagerScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    // Background Gradient to match SwiftUI theme
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2B72D1), Color(0xFF1A5BB5))
    )

    Box(modifier = Modifier.fillMaxSize().background(gradient)) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> WelcomePage(onNext = { scope.launch { pagerState.animateScrollToPage(1) } })
                1 -> FinancialGoalsPage(onNext = { scope.launch { pagerState.animateScrollToPage(2) } })
                2 -> PrimaryGoalsPage(onGetStarted = onFinish)
            }
        }

        // Indicator dots at bottom
        OnboardingFooter(
            currentPage = pagerState.currentPage,
            onSkip = onFinish,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun OnboardingFooter(currentPage: Int, onSkip: () -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 30.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(50.dp)) // To center dots

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { index ->
                val alpha = if (currentPage == index) 1f else 0.3f
                Box(modifier = Modifier.size(8.dp).background(Color.White.copy(alpha = alpha), androidx.compose.foundation.shape.CircleShape))
            }
        }

        TextButton(onClick = onSkip) {
            Text("Skip", color = Color.White.copy(alpha = 0.8f))
        }
    }
}