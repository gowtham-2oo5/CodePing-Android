package com.codeping.android_client.presentation.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PageIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isActive = index == currentPage
            
            val color by animateColorAsState(
                targetValue = if (isActive) activeColor else inactiveColor,
                animationSpec = tween(300),
                label = "indicator_color"
            )
            
            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.2f else 1f,
                animationSpec = tween(300),
                label = "indicator_scale"
            )
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
