package com.letthemcook.auth.ui.navigation

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
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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

        RegistrationScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    RegistrationUiAction.NavigateToLogin -> navController.navigate(AuthNavRoutes.Login)
                    //TODO temporary
                    RegistrationUiAction.Register -> navController.navigate(logInRoute)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<AuthNavRoutes.Login> {
        val viewModel = koinViewModel<LoginViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        LoginScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    LoginUiAction.NavigateToRegistration -> navController.navigate(AuthNavRoutes.Registration)
                    //TODO temporary
                    LoginUiAction.Login -> navController.navigate(logInRoute)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
}