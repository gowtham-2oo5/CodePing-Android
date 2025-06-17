package com.codeping.android_client.data.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import java.util.UUID

object GitHubAuthHelper {
    private const val GITHUB_CLIENT_ID = "your_github_client_id_here"
    private const val REDIRECT_URI = "codeping://github-auth"
    
    fun startGitHubAuth(context: Context): String {
        val state = UUID.randomUUID().toString()
        
        val authUrl = "https://github.com/login/oauth/authorize?" +
                "client_id=$GITHUB_CLIENT_ID" +
                "&redirect_uri=$REDIRECT_URI" +
                "&scope=user:email" +
                "&state=$state"
        
        // Use Chrome Custom Tabs for better UX
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        
        customTabsIntent.launchUrl(context, Uri.parse(authUrl))
        
        return state
    }
    
    fun handleAuthCallback(intent: Intent): GitHubAuthResult {
        val uri = intent.data
        if (uri != null && uri.scheme == "codeping" && uri.host == "github-auth") {
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            val error = uri.getQueryParameter("error")
            
            return when {
                error != null -> GitHubAuthResult.Error(error)
                code != null -> GitHubAuthResult.Success(code, state)
                else -> GitHubAuthResult.Error("Invalid callback")
            }
        }
        return GitHubAuthResult.Error("Invalid callback URI")
    }
}

sealed class GitHubAuthResult {
    data class Success(val code: String, val state: String?) : GitHubAuthResult()
    data class Error(val message: String) : GitHubAuthResult()
}
