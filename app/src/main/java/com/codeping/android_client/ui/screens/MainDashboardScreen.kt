package com.codeping.android_client.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.codeping.android_client.ui.navigation.CodePingBottomNavigation
import com.codeping.android_client.ui.navigation.bottomNavItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onLogout: () -> Unit,
    themeManager: com.codeping.android_client.ui.theme.ThemeManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "CodePing",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    com.codeping.android_client.ui.components.CompactThemeSwitcher(
                        isDarkTheme = themeManager.isDarkTheme,
                        onThemeToggle = { themeManager.toggleTheme() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            CodePingBottomNavigation(
                selectedRoute = currentRoute,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        // Pop up to the start destination to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(onLogout = onLogout, themeManager = themeManager)
            }
            composable("contests") {
                ContestsScreen()
            }
            composable("share") {
                ShareProfileScreen()
            }
            composable("stats") {
                StatsScreen()
            }
            composable("profile") {
                ProfileScreen(onLogout = onLogout, themeManager = themeManager)
            }
        }
    }
}

// Placeholder screens - we'll implement these properly later
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    themeManager: com.codeping.android_client.ui.theme.ThemeManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🏠 Home Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Welcome to CodePing! Your competitive programming journey starts here.")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Theme switcher demo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Theme: ")
            Spacer(modifier = Modifier.width(16.dp))
            com.codeping.android_client.ui.components.ThemeSwitcher(
                isDarkTheme = themeManager.isDarkTheme,
                onThemeToggle = { themeManager.toggleTheme() }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Logout (Temp)")
        }
    }
}

@Composable
fun ContestsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🏆 Contests",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Upcoming contests from LeetCode, Codeforces, and CodeChef will appear here.")
    }
}

@Composable
fun ShareProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📤 Share Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Share your CodePing profile and achievements with friends!")
    }
}

@Composable
fun StatsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📊 Statistics",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your performance analytics and progress tracking will be displayed here.")
    }
}

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    themeManager: com.codeping.android_client.ui.theme.ThemeManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "👤 Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Manage your account settings and preferences.")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Theme preference
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (themeManager.isDarkTheme) "Dark Mode" else "Light Mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                com.codeping.android_client.ui.components.CompactThemeSwitcher(
                    isDarkTheme = themeManager.isDarkTheme,
                    onThemeToggle = { themeManager.toggleTheme() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}
