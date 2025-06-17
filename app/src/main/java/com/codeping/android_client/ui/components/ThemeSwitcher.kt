package com.codeping.android_client.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSwitcher(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isDarkTheme) 180f else 0f,
        animationSpec = tween(500),
        label = "theme_rotation"
    )
    
    Surface(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable { onThemeToggle() },
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
fun CompactThemeSwitcher(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onThemeToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
