package com.codeping.android_client.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.codeping.android_client.utils.SecurePreferences
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codeping.android_client.R
import com.codeping.android_client.data.auth.UserDisplayInfo
import com.codeping.android_client.presentation.auth.AuthViewModel
import com.codeping.android_client.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(context) }
    val uiState by authViewModel.uiState.collectAsState()
    
    var showSignOutDialog by remember { mutableStateOf(false) }
    
    // Handle sign out completion
    LaunchedEffect(uiState.signOutMessage) {
        uiState.signOutMessage?.let {
            authViewModel.clearSignOutMessage()
            onSignOut()
        }
    }
    
    // Sign out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.sign_out),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.sign_out_confirmation),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        authViewModel.signOut()
                    },
                    enabled = !uiState.isSigningOut
                ) {
                    if (uiState.isSigningOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.confirm))
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSignOutDialog = false },
                    enabled = !uiState.isSigningOut
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = EmeraldPrimary
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showSignOutDialog = true },
                        enabled = !uiState.isSigningOut
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = stringResource(R.string.sign_out),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // User Profile Card
            uiState.userDisplayInfo?.let { userInfo ->
                UserProfileCard(userInfo = userInfo)
            }
            
            // Welcome Message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldLight.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Welcome to CodePing!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = EmeraldPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Your competitive programming journey starts here. Track contests, monitor progress, and never miss an opportunity to code!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Feature Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    title = stringResource(R.string.contests),
                    icon = "🏆",
                    description = "Upcoming contests",
                    modifier = Modifier.weight(1f)
                )
                
                FeatureCard(
                    title = stringResource(R.string.stats),
                    icon = "📊",
                    description = "Your progress",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    title = stringResource(R.string.profile),
                    icon = "👤",
                    description = "Share achievements",
                    modifier = Modifier.weight(1f)
                )
                
                FeatureCard(
                    title = stringResource(R.string.settings),
                    icon = "⚙️",
                    description = "Customize app",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Coming Soon Message
            Text(
                text = "🚀 More features coming soon!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UserProfileCard(
    userInfo: UserDisplayInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            EmeraldPrimary.copy(alpha = 0.1f),
                            EmeraldSecondary.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Picture Placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White), // Changed to white background for better contrast
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.codeping_logo),
                        contentDescription = "Profile",
                        tint = Color.Unspecified, // Use original colors from vector drawable
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // User Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = userInfo.displayName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (userInfo.email.isNotEmpty()) {
                        Text(
                            text = userInfo.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Auth Provider Badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = EmeraldPrimary.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "via ${SecurePreferences.getAuthProvider(LocalContext.current) ?: "Unknown"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    icon: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
