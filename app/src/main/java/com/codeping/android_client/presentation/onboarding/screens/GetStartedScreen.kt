package com.codeping.android_client.presentation.onboarding.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codeping.android_client.ui.theme.*

@Composable
fun GetStartedScreen(
    onGoogleSignIn: () -> Unit,
    onGitHubSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Ready to Start?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Sign in to sync your data across devices and never lose your progress",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Sign-in Options
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Google Sign In
            SignInButton(
                text = "Continue with Google",
                icon = Icons.Default.Email, // Replace with Google icon
                backgroundColor = Color.White,
                contentColor = Color.Black,
                borderColor = Color(0xFFDDDDDD),
                onClick = onGoogleSignIn
            )
            
            // GitHub Sign In
            SignInButton(
                text = "Continue with GitHub",
                icon = Icons.Default.Email, // Replace with GitHub icon
                backgroundColor = Color(0xFF24292E),
                contentColor = Color.White,
                onClick = onGitHubSignIn
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Benefits Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = EmeraldLight.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Why sign in?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = EmeraldPrimary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                BenefitItem(
                    icon = "☁️",
                    text = "Sync data across all your devices"
                )
                
                BenefitItem(
                    icon = "🔔",
                    text = "Personalized contest notifications"
                )
                
                BenefitItem(
                    icon = "📊",
                    text = "Detailed progress analytics"
                )
                
                BenefitItem(
                    icon = "🔗",
                    text = "Shareable profile with achievements"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Privacy Note
        Text(
            text = "We respect your privacy. Your data is secure and never shared with third parties.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun SignInButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = borderColor?.let { 
            BorderStroke(1.dp, it)
        },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun BenefitItem(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
