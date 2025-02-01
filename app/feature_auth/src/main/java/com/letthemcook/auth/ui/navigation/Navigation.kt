package com.letthemcook.auth.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.letthemcook.auth.domain.viewModels.login.LoginUiAction
import com.letthemcook.auth.domain.viewModels.login.LoginViewModel
import com.letthemcook.auth.domain.viewModels.registration.RegistrationUiAction
import com.letthemcook.auth.domain.viewModels.registration.RegistrationViewModel
import com.letthemcook.auth.ui.screens.LoginScreen
import com.letthemcook.auth.ui.screens.RegistrationScreen
import com.letthemcook.core.domain.model.auth.login.LoginAuthResult
import com.letthemcook.core.domain.model.auth.registration.RegistrationAuthResult
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface AuthNavRoutes {
    @Serializable data object Registration : AuthNavRoutes
    @Serializable data object Login : AuthNavRoutes
}

fun NavGraphBuilder.addAuthRoutes(
    navController: NavController,
    logInRoute: Any
) {
    composable<AuthNavRoutes.Registration> {
        val viewModel = koinViewModel<RegistrationViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState.registrationResult) {
            if (uiState.registrationResult == RegistrationAuthResult.Successful) {
                navController.navigate(logInRoute)
            }
        }

        RegistrationScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    RegistrationUiAction.NavigateToLogin -> navController.navigate(AuthNavRoutes.Login)
                    RegistrationUiAction.Register -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<AuthNavRoutes.Login> {
        val viewModel = koinViewModel<LoginViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState.loginResult) {
            if (uiState.loginResult == LoginAuthResult.Successful) {
                navController.navigate(logInRoute)
            }
        }

        LoginScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    LoginUiAction.NavigateToRegistration -> navController.navigate(AuthNavRoutes.Registration)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
}