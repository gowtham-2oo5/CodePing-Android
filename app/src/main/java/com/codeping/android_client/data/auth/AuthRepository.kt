package com.codeping.android_client.data.auth

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.codeping.android_client.utils.ErrorLogger
import com.codeping.android_client.utils.SecurePreferences
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    
    // Note: Google Sign-In is handled in MainActivity due to UI requirements
    // This method is kept for consistency but actual sign-in happens in MainActivity
    suspend fun signInWithGoogle(): AuthResult {
        return try {
            // Check if user is already signed in with Firebase
            val currentUser = auth.currentUser
            if (currentUser != null) {
                logUserData(currentUser, "Google")
                saveUserToPreferences(currentUser, "Google")
                return AuthResult.Success(currentUser, "Already signed in with Google")
            }
            
            // Google Sign-In flow is handled in MainActivity's activity result
            AuthResult.Error("Google Sign-In is handled through MainActivity")
            
        } catch (e: Exception) {
            ErrorLogger.logAuthError(
                context = context,
                error = e,
                authMethod = "Google",
                userJourney = "Google Sign-In attempt"
            )
            AuthResult.Error("Google authentication failed: ${e.localizedMessage}")
        }
    }
    
    suspend fun signInWithGitHub(): AuthResult {
        return try {
            // Check if user is already signed in with Firebase
            val currentUser = auth.currentUser
            if (currentUser != null) {
                logUserData(currentUser, "GitHub")
                saveUserToPreferences(currentUser, "GitHub")
                return AuthResult.Success(currentUser, "Already signed in with GitHub")
            }
            
            // GitHub Sign-In flow is handled in MainActivity due to UI requirements
            AuthResult.Error("GitHub Sign-In is handled through MainActivity")
            
        } catch (e: Exception) {
            ErrorLogger.logAuthError(
                context = context,
                error = e,
                authMethod = "GitHub",
                userJourney = "GitHub Sign-In attempt"
            )
            AuthResult.Error("GitHub authentication failed: ${e.localizedMessage}")
        }
    }
    
    suspend fun signOut(): AuthResult {
        return try {
            auth.signOut()
            SecurePreferences.clearUserData(context)
            AuthResult.Success(null, "Signed out successfully")
        } catch (e: Exception) {
            ErrorLogger.logAuthError(
                context = context,
                error = e,
                authMethod = "SignOut",
                userJourney = "User signing out"
            )
            AuthResult.Error("Sign out failed: ${e.localizedMessage}")
        }
    }
    
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun getUserDisplayInfo(): UserDisplayInfo? {
        val user = getCurrentUser()
        return if (user != null) {
            UserDisplayInfo(
                displayName = user.displayName ?: "User",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString()
            )
        } else {
            // Try to get from secure preferences if Firebase user is null
            if (SecurePreferences.isLoggedIn(context)) {
                UserDisplayInfo(
                    displayName = SecurePreferences.getUserName(context) ?: "User",
                    email = SecurePreferences.getUserEmail(context) ?: "",
                    photoUrl = SecurePreferences.getUserPhotoUrl(context)
                )
            } else {
                null
            }
        }
    }
    
    private fun logUserData(user: FirebaseUser, provider: String) {
        Log.d("AuthRepository", """
            User signed in successfully:
            Provider: $provider
            UID: ${user.uid}
            Email: ${user.email}
            Display Name: ${user.displayName}
            Photo URL: ${user.photoUrl}
        """.trimIndent())
    }
    
    private fun saveUserToPreferences(user: FirebaseUser, provider: String) {
        SecurePreferences.saveUserData(
            context = context,
            userId = user.uid,
            email = user.email,
            name = user.displayName,
            photoUrl = user.photoUrl?.toString(),
            authProvider = provider
        )
    }
}

sealed class AuthResult {
    data class Success(val user: FirebaseUser?, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

data class UserDisplayInfo(
    val displayName: String,
    val email: String,
    val photoUrl: String?
)
