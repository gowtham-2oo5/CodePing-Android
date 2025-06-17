package com.codeping.android_client.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codeping.android_client.presentation.onboarding.components.OnboardingButton
import com.codeping.android_client.presentation.onboarding.components.OnboardingTextButton
import com.codeping.android_client.presentation.onboarding.components.PageIndicator
import com.codeping.android_client.presentation.onboarding.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = remember { OnboardingViewModel(context) }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { OnboardingViewModel.TOTAL_PAGES })
    val uiState by viewModel.uiState.collectAsState()
    
    // Sync pager state with view model
    LaunchedEffect(pagerState.currentPage) {
        viewModel.goToPage(pagerState.currentPage)
    }
    
    // Handle onboarding completion
    LaunchedEffect(uiState.hasCompletedOnboarding) {
        if (uiState.hasCompletedOnboarding) {
            onOnboardingComplete()
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status bar spacer
            Spacer(modifier = Modifier.height(24.dp))
            
            // Skip button (only on first 2 pages)
            if (uiState.currentPage < OnboardingViewModel.TOTAL_PAGES - 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OnboardingTextButton(
                        text = "Skip",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(OnboardingViewModel.TOTAL_PAGES - 1)
                            }
                        }
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
            
            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> WelcomeScreen()
                    1 -> FeaturesScreen()
                    2 -> PlatformsScreen()
                }
            }
            
            // Bottom section with indicator and navigation
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicator
                PageIndicator(
                    totalPages = OnboardingViewModel.TOTAL_PAGES,
                    currentPage = uiState.currentPage
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Navigation buttons
                AnimatedVisibility(
                    visible = uiState.currentPage < OnboardingViewModel.TOTAL_PAGES - 1,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (uiState.currentPage == 0) {
                            Arrangement.End
                        } else {
                            Arrangement.SpaceBetween
                        }
                    ) {
                        // Back button (hidden on first page)
                        if (uiState.currentPage > 0) {
                            OnboardingTextButton(
                                text = "Back",
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(uiState.currentPage - 1)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        
                        // Next button
                        OnboardingButton(
                            text = "Next",
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(uiState.currentPage + 1)
                                }
                            },
                            modifier = Modifier.weight(2f)
                        )
                    }
                }
                
                // Get Started button (only on last page)
                AnimatedVisibility(
                    visible = uiState.currentPage == OnboardingViewModel.TOTAL_PAGES - 1,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    OnboardingButton(
                        text = "Get Started",
                        onClick = { viewModel.completeOnboarding() },
                        isLoading = uiState.isLoading
                    )
                }
            }
        }
    }
}
