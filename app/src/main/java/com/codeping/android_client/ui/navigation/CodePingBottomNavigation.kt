package com.codeping.android_client.ui.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodePingBottomNavigation(
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = bottomNavItems.indexOfFirst { it.route == selectedRoute }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column {
            // Animated tracing line
            AnimatedTracingLine(
                selectedIndex = selectedIndex,
                itemCount = bottomNavItems.size
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 4.dp), // Reduced from 8dp to 6dp
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    BottomNavItem(
                        item = item,
                        isSelected = selectedRoute == item.route,
                        onClick = { onItemSelected(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedTracingLine(
    selectedIndex: Int,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val primaryColor = MaterialTheme.colorScheme.primary // Get color outside Canvas
    
    // Animate the line position
    val animatedPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tracing_line_position"
    )
    
    // Animate the line width for a cool effect
    val animatedWidth by animateFloatAsState(
        targetValue = if (selectedIndex >= 0) 1f else 0f,
        animationSpec = tween(300),
        label = "tracing_line_width"
    )
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
    ) {
        if (selectedIndex >= 0 && itemCount > 0) {
            val itemWidth = size.width / itemCount
            val lineWidth = itemWidth * 0.6f * animatedWidth // 60% of item width
            val centerX = (animatedPosition * itemWidth) + (itemWidth / 2f)
            val startX = centerX - (lineWidth / 2f)
            val endX = centerX + (lineWidth / 2f)
            
            drawLine(
                color = primaryColor, // Use the color we got outside
                start = Offset(startX, size.height / 2f),
                end = Offset(endX, size.height / 2f),
                strokeWidth = with(density) { 3.dp.toPx() },
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp) // Reduced from 8dp to 6dp
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Special styling for Share button (center)
        if (item.isSpecial) {
            ShareButton(
                isSelected = isSelected,
                icon = if (isSelected) item.selectedIcon else item.unselectedIcon
            )
        } else {
            RegularNavItem(
                item = item,
                isSelected = isSelected
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                item.isSpecial && isSelected -> Color.White
                item.isSpecial -> MaterialTheme.colorScheme.primary
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun ShareButton(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Share",
            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun RegularNavItem(
    item: BottomNavItem,
    isSelected: Boolean
) {
    Box {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            tint = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        // News badge for contests
        if (item.hasNews && item.route == "contests") {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
            )
        }
    }
}
