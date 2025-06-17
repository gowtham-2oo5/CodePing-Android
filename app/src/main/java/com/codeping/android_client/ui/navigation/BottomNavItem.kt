package com.codeping.android_client.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean = false,
    val isSpecial: Boolean = false // For the center share button
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    object Contests : BottomNavItem(
        route = "contests", 
        title = "Contests",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents,
        hasNews = true // Show badge for new contests
    )
    
    object Share : BottomNavItem(
        route = "share",
        title = "Share",
        selectedIcon = Icons.Filled.Share,
        unselectedIcon = Icons.Outlined.Share,
        isSpecial = true // Center highlight button
    )
    
    object Stats : BottomNavItem(
        route = "stats",
        title = "Stats", 
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Outlined.Analytics
    )
    
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Contests,
    BottomNavItem.Share,
    BottomNavItem.Stats,
    BottomNavItem.Profile
)
