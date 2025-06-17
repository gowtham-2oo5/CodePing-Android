package com.codeping.android_client.presentation.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeping.android_client.data.auth.AuthRepository
import com.codeping.android_client.data.auth.AuthResult
import com.codeping.android_client.data.auth.UserDisplayInfo
import com.codeping.android_client.utils.SecurePreferences
import com.codeping.android_client.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val authMethod: String? = null,
    val userDisplayInfo: UserDisplayInfo? = null,
    val isSigningOut: Boolean = false,
    val signOutMessage: String? = null
)

class AuthViewModel(val context: Context) : ViewModel() {
    
    private val authRepository = AuthRepository(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // Firebase Auth State Listener
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        Log.d("AuthViewModel", "Firebase auth state changed: user = ${user?.email}")
        
        if (user != null) {
            // User is signed in
            val userInfo = UserDisplayInfo(
                displayName = user.displayName ?: SecurePreferences.getUserName(context) ?: "User",
                email = user.email ?: SecurePreferences.getUserEmail(context) ?: "",
                photoUrl = user.photoUrl?.toString() ?: SecurePreferences.getUserPhotoUrl(context)
            )
            
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                userDisplayInfo = userInfo,
                isLoading = false,
                errorMessage = null
            )
        } else {
            // User is signed out
            if (_uiState.value.isAuthenticated) {
                Log.d("AuthViewModel", "User signed out, updating state")
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = false,
                    userDisplayInfo = null,
                    authMethod = null,
                    isLoading = false
                )
            }
        }
    }
    
    init {
        // Add Firebase auth state listener
        firebaseAuth.addAuthStateListener(authStateListener)
        
        // Check initial auth state
        checkAuthState()
    }
    
    override fun onCleared() {
        super.onCleared()
        // Remove Firebase auth state listener
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                // Check both Firebase and SecurePreferences
                val firebaseUser = firebaseAuth.currentUser
                val isLoggedInLocally = SecurePreferences.isLoggedIn(context) && !SecurePreferences.isSessionExpired(context)
                
                Log.d("AuthViewModel", "Initial auth check - Firebase: ${firebaseUser?.email}, Local: $isLoggedInLocally")
                
                if (firebaseUser != null && isLoggedInLocally) {
                    // User is authenticated
                    val userInfo = UserDisplayInfo(
                        displayName = firebaseUser.displayName ?: SecurePreferences.getUserName(context) ?: "User",
                        email = firebaseUser.email ?: SecurePreferences.getUserEmail(context) ?: "",
                        photoUrl = firebaseUser.photoUrl?.toString() ?: SecurePreferences.getUserPhotoUrl(context)
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        userDisplayInfo = userInfo,
                        authMethod = SecurePreferences.getAuthProvider(context)
                    )
                    
                    // Update last login
                    SecurePreferences.updateLastLogin(context)
                } else {
                    // User is not authenticated
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        userDisplayInfo = null,
                        authMethod = null
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error checking auth state", e)
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = false,
                    errorMessage = "Error checking authentication state"
                )
            }
        }
    }
    
    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
    
    fun signInWithGoogle() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
        
        // Trigger Google Sign-In through MainActivity
        if (context is MainActivity) {
            context.startGoogleSignIn()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Google Sign-In requires MainActivity context"
            )
        }
    }
    
    // This method will be called from MainActivity when Google Sign-In completes
    fun onGoogleSignInResult(success: Boolean, errorMessage: String? = null) {
        if (success) {
            // Don't update state here - let Firebase auth state listener handle it
            Log.d("AuthViewModel", "Google sign-in successful, waiting for Firebase auth state")
            _uiState.value = _uiState.value.copy(
                authMethod = "Google",
                errorMessage = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = errorMessage ?: "Google Sign-In failed"
            )
        }
    }
    
    fun signInWithGitHub() {
        Log.d("AuthViewModel", "GitHub sign-in requested")
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )
        
        // Trigger GitHub Sign-In through MainActivity
        if (context is MainActivity) {
            context.startGitHubSignIn()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "GitHub Sign-In requires MainActivity context"
            )
        }
    }
    
    // This method will be called from MainActivity when GitHub Sign-In completes
    fun onGitHubSignInResult(success: Boolean, errorMessage: String? = null) {
        if (success) {
            // Don't update state here - let Firebase auth state listener handle it
            Log.d("AuthViewModel", "GitHub sign-in successful, waiting for Firebase auth state")
            _uiState.value = _uiState.value.copy(
                authMethod = "GitHub",
                errorMessage = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = errorMessage ?: "GitHub Sign-In failed"
            )
        }
    }
    
    fun skipAuth() {
        _uiState.value = _uiState.value.copy(
            isAuthenticated = true,
            authMethod = "Skipped",
            isLoading = false
        )
    }
    
    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSigningOut = true,
                signOutMessage = null
            )
            
            try {
                // Clear local data first
                SecurePreferences.clearUserData(context)
                
                // Sign out from Firebase (this will trigger the auth state listener)
                firebaseAuth.signOut()
                
                _uiState.value = _uiState.value.copy(
                    isSigningOut = false,
                    signOutMessage = "Signed out successfully"
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error during sign out", e)
                _uiState.value = _uiState.value.copy(
                    isSigningOut = false,
                    errorMessage = "Sign out failed: ${e.localizedMessage}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearSignOutMessage() {
        _uiState.value = _uiState.value.copy(signOutMessage = null)
    }
    
    fun getCurrentUser() = authRepository.getCurrentUser()
}
