package com.codeping.android_client.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.codeping.android_client.presentation.auth.AuthScreen
import com.codeping.android_client.presentation.onboarding.screens.*
import com.codeping.android_client.ui.theme.CodePingTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    CodePingTheme {
        WelcomeScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeaturesScreenPreview() {
    CodePingTheme {
        FeaturesScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlatformsScreenPreview() {
    CodePingTheme {
        PlatformsScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    CodePingTheme {
        OnboardingScreen(
            onOnboardingComplete = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthScreenPreview() {
    CodePingTheme {
        AuthScreen(
            onAuthSuccess = {},
            onSkipAuth = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark Theme")
@Composable
fun OnboardingScreenDarkPreview() {
    CodePingTheme(darkTheme = true) {
        OnboardingScreen(
            onOnboardingComplete = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Auth Dark Theme")
@Composable
fun AuthScreenDarkPreview() {
    CodePingTheme(darkTheme = true) {
        AuthScreen(
            onAuthSuccess = {},
            onSkipAuth = {}
        )
    }
}
