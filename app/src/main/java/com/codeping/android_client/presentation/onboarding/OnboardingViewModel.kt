package com.codeping.android_client.presentation.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val hasCompletedOnboarding: Boolean = false
)

class OnboardingViewModel(private val context: Context) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    fun nextPage() {
        val currentState = _uiState.value
        if (currentState.currentPage < TOTAL_PAGES - 1) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage + 1
            )
        }
    }
    
    fun previousPage() {
        val currentState = _uiState.value
        if (currentState.currentPage > 0) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage - 1
            )
        }
    }
    
    fun goToPage(page: Int) {
        if (page in 0 until TOTAL_PAGES) {
            _uiState.value = _uiState.value.copy(currentPage = page)
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Save onboarding completion status
            val prefs = context.getSharedPreferences("codeping_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("onboarding_completed", true).apply()
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                hasCompletedOnboarding = true
            )
        }
    }
    
    companion object {
        const val TOTAL_PAGES = 3 // Reduced from 4 since auth is separate
    }
}
