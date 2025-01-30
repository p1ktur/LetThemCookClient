package com.letthemcook.profile.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.letthemcook.core.domain.model.data.file.File
import com.letthemcook.core.ui.navigation.NavBarRoutes
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiAction
import com.letthemcook.profile.domain.viewModels.profile.ProfileViewModel
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeUiAction
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeViewModel
import com.letthemcook.profile.domain.viewModels.settings.SettingsUiAction
import com.letthemcook.profile.domain.viewModels.settings.SettingsViewModel
import com.letthemcook.profile.ui.screens.PasswordChangeScreen
import com.letthemcook.profile.ui.screens.ProfileScreen
import com.letthemcook.profile.ui.screens.SettingsScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface ProfileNavRoutes {
    @Serializable data object Profile : ProfileNavRoutes
    @Serializable data object Settings : ProfileNavRoutes
    @Serializable data object PasswordChange : ProfileNavRoutes
}

fun NavGraphBuilder.addProfileRoutes(
    navController: NavController,
    logOutRoute: Any,
    navBarRoutes: NavBarRoutes,
    onViewMedia: (File) -> Unit
) {
    composable<ProfileNavRoutes.Profile> {
        val viewModel = koinInject<ProfileViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        ProfileScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    ProfileUiAction.NavigateBack -> navController.navigateUp()
                    ProfileUiAction.NavigateToHome -> navController.navigate(navBarRoutes.homeRoute)
                    ProfileUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    ProfileUiAction.NavigateToSettings -> navController.navigate(ProfileNavRoutes.Settings)

                    is ProfileUiAction.ViewMediaFile -> onViewMedia(action.file)

                    ProfileUiAction.ChangePassword -> navController.navigate(ProfileNavRoutes.PasswordChange)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<ProfileNavRoutes.Settings> {
        val viewModel = koinInject<SettingsViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        SettingsScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    SettingsUiAction.NavigateBack -> navController.navigateUp()
                    SettingsUiAction.NavigateToHome -> navController.navigate(navBarRoutes.homeRoute)
                    SettingsUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    SettingsUiAction.NavigateToProfile -> navController.navigateUp()
                    SettingsUiAction.LogOut -> navController.navigate(logOutRoute)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<ProfileNavRoutes.PasswordChange> {
        val viewModel = koinInject<PasswordChangeViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        PasswordChangeScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    PasswordChangeUiAction.NavigateBack -> navController.navigateUp()
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
}