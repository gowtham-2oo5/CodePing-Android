package com.codeping.android_client.presentation.onboarding.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codeping.android_client.R
import com.codeping.android_client.ui.theme.EmeraldLight
import com.codeping.android_client.ui.theme.EmeraldPrimary

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Logo Section using Material Design Card
        Card(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = EmeraldLight.copy(alpha = 0.2f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.codeping_logo),
                    contentDescription = "CodePing Logo",
                    modifier = Modifier.size(80.dp),
                    tint = Color.Unspecified // Use original colors from vector drawable
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // App Name
        Text(
            text = "CodePing",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = EmeraldPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tagline
        Text(
            text = "Competitive Programming Tracker",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Welcome Message
        Text(
            text = "Track contests, monitor progress, and never miss coding opportunities!",
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Feature Pills using Material Design Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeaturePill(
                icon = "🔔",
                text = "Alerts"
            )
            FeaturePill(
                icon = "📊", 
                text = "Stats"
            )
            FeaturePill(
                icon = "🔗",
                text = "Share"
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FeaturePill(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { /* No action needed for onboarding */ },
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = EmeraldLight.copy(alpha = 0.2f),
            labelColor = EmeraldPrimary
        ),
        border = BorderStroke(width = 2.dp,color = EmeraldPrimary.copy(alpha = 0.3f))
    )
}
