package com.yogesh.auraai.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yogesh.auraai.core.di.AppContainer
import com.yogesh.auraai.domain.model.UserSettings
import com.yogesh.auraai.presentation.chat.ChatScreen
import com.yogesh.auraai.presentation.conversations.ConversationListScreen

import com.yogesh.auraai.presentation.home.AuraLogo
import com.yogesh.auraai.presentation.home.AuraState
import com.yogesh.auraai.presentation.onboarding.OnboardingScreen
import com.yogesh.auraai.presentation.settings.SettingsScreen
import kotlinx.coroutines.delay
import java.time.format.TextStyle

@Composable
fun AuraNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
) {
    val settings by appContainer.settingsRepository.observeSettings()
        .collectAsStateWithLifecycle(initialValue = UserSettings())

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                isOnboardingComplete = settings.isOnboardingComplete,
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                appContainer = appContainer,
                onComplete = {
                    navController.navigate(Routes.CONVERSATIONS) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.CONVERSATIONS) {
            ConversationListScreen(
                appContainer = appContainer,
                onNavigateToChat = { conversationId ->
                    navController.navigate(Routes.chat(conversationId))
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
            ChatScreen(
                conversationId = conversationId,
                appContainer = appContainer,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                appContainer = appContainer,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOnboarding = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}

@Composable
private fun SplashScreen(
    isOnboardingComplete: Boolean,
    onNavigate: (String) -> Unit,
) {
    LaunchedEffect(isOnboardingComplete) {
        delay(1500)

        val destination =
            if (isOnboardingComplete) {
                Routes.CONVERSATIONS
            } else {
                Routes.ONBOARDING
            }

        onNavigate(destination)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050816)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AuraLogo(
                state = AuraState.Idle
            )
            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = "AuraAI",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                style = androidx.compose.ui.text.TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6),
                            Color(0xFF3B82F6),
                            Color(0xFF22D3EE)
                        )
                    )
                )
            )
            Text(
                text = "Your Intelligent Companion",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}
