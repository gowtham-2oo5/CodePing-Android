package com.codeping.android_client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.codeping.android_client.presentation.auth.AuthScreen
import com.codeping.android_client.presentation.auth.AuthViewModel
import com.codeping.android_client.presentation.main.MainScreen
import com.codeping.android_client.presentation.onboarding.OnboardingScreen
import com.codeping.android_client.ui.theme.CodePingTheme
import com.codeping.android_client.utils.SecurePreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

class MainActivity : ComponentActivity() {
    
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth = FirebaseAuth.getInstance()
    private lateinit var authViewModel: AuthViewModel
    
    // Activity result launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            handleGoogleSignInResult(account)
        } catch (e: ApiException) {
            Log.e("MainActivity", "Google sign-in failed", e)
            authViewModel.onGoogleSignInResult(false, "Google sign-in failed: ${e.localizedMessage}")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super.onCreate()
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Google Sign-In
        initializeGoogleSignIn()
        
        // Initialize AuthViewModel early
        authViewModel = AuthViewModel(this)
        
        // Make status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Keep splash screen visible while loading
        var isLoading by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { isLoading }
        
        setContent {
            val themeManager = com.codeping.android_client.ui.theme.rememberThemeManager()
            
            CodePingTheme(darkTheme = themeManager.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(Unit) {
                        // Simulate loading time for splash screen
                        delay(1500)
                        isLoading = false
                    }
                    
                    if (!isLoading) {
                        CodePingApp(themeManager = themeManager)
                    }
                }
            }
        }
    }
    
    private fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    
    fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
    
    fun startGitHubSignIn() {
        Log.d("MainActivity", "Starting GitHub sign-in")
        
        val provider = OAuthProvider.newBuilder("github.com")
            .setScopes(listOf("user:email"))
            .build()
        
        // Update loading state in AuthViewModel
        authViewModel.setLoading(true)
        
        auth.startActivityForSignInWithProvider(this, provider)
            .addOnSuccessListener { result ->
                Log.d("MainActivity", "GitHub sign-in successful")
                val user = result.user
                if (user != null) {
                    // Save user data synchronously first (to avoid complexity)
                    SecurePreferences.saveUserData(
                        context = this,
                        userId = user.uid,
                        email = user.email,
                        name = user.displayName,
                        photoUrl = user.photoUrl?.toString(),
                        authProvider = "GitHub"
                    )
                    // Notify success
                    authViewModel.onGitHubSignInResult(true)
                } else {
                    Log.e("MainActivity", "GitHub user is null")
                    authViewModel.onGitHubSignInResult(false, "Authentication succeeded but user data is missing")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "GitHub sign-in failed: ${exception.message}", exception)
                authViewModel.onGitHubSignInResult(false, "GitHub sign-in failed: ${exception.localizedMessage}")
            }
    }
    
    private fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("MainActivity", "Google sign-in successful")
                        val user = auth.currentUser
                        if (user != null) {
                            // Save user data synchronously
                            SecurePreferences.saveUserData(
                                context = this,
                                userId = user.uid,
                                email = user.email,
                                name = user.displayName,
                                photoUrl = user.photoUrl?.toString(),
                                authProvider = "Google"
                            )
                            // Notify AuthViewModel of success
                            authViewModel.onGoogleSignInResult(true)
                        } else {
                            authViewModel.onGoogleSignInResult(false, "User is null after successful authentication")
                        }
                    } else {
                        Log.e("MainActivity", "Firebase authentication failed", task.exception)
                        authViewModel.onGoogleSignInResult(false, "Firebase authentication failed: ${task.exception?.localizedMessage}")
                    }
                }
        } else {
            authViewModel.onGoogleSignInResult(false, "Google account is null")
        }
    }
    
    @Composable
    fun CodePingApp(themeManager: com.codeping.android_client.ui.theme.ThemeManager) {
        // Observe auth state from ViewModel using collectAsStateWithLifecycle
        val authState by authViewModel.uiState.collectAsStateWithLifecycle()
        
        // Track onboarding completion state
        var onboardingCompleted by remember { 
            mutableStateOf(hasCompletedOnboarding()) 
        }
        
        // Determine app state based on auth state and onboarding
        val appState by remember {
            derivedStateOf {
                when {
                    !onboardingCompleted -> AppState.ONBOARDING
                    authState.isAuthenticated -> AppState.MAIN_APP
                    else -> AppState.AUTH
                }
            }
        }
        
        // Log state changes for debugging
        LaunchedEffect(appState, authState.isAuthenticated, onboardingCompleted) {
            Log.d("MainActivity", "App State: $appState, Auth: ${authState.isAuthenticated}, Onboarding: $onboardingCompleted")
        }
        
        when (appState) {
            AppState.ONBOARDING -> {
                OnboardingScreen(
                    onOnboardingComplete = {
                        Log.d("MainActivity", "Onboarding completed callback triggered")
                        // Mark onboarding as completed
                        getSharedPreferences("codeping_prefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("onboarding_completed", true)
                            .apply()
                        // Update local state to trigger recomposition
                        onboardingCompleted = true
                    }
                )
            }
            
            AppState.AUTH -> {
                AuthScreen(
                    onAuthSuccess = {
                        // Navigation will happen automatically via state observation
                        Log.d("MainActivity", "Auth success callback triggered")
                    },
                    onSkipAuth = {
                        // Navigation will happen automatically via state observation
                        Log.d("MainActivity", "Skip auth callback triggered")
                    },
                    viewModel = authViewModel
                )
            }
            
            AppState.MAIN_APP -> {
                com.codeping.android_client.ui.screens.MainDashboardScreen(
                    onLogout = {
                        // Clear auth state through ViewModel
                        authViewModel.signOut()
                        // Clear Google Sign-In
                        googleSignInClient.signOut()
                        // Clear Firebase auth
                        auth.signOut()
                    },
                    themeManager = themeManager
                )
            }
        }
    }
    
    private fun hasCompletedOnboarding(): Boolean {
        return getSharedPreferences("codeping_prefs", MODE_PRIVATE)
            .getBoolean("onboarding_completed", false)
    }
}

enum class AppState {
    ONBOARDING,
    AUTH,
    MAIN_APP
}