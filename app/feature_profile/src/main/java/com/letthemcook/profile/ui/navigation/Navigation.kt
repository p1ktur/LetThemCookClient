package com.letthemcook.profile.ui.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letthemcook.core.data.remote.authorization.AuthManager
import com.letthemcook.core.domain.model.auth.passwordChange.PasswordChangeResult
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.ui.navigation.NavBarRoutes
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileUiAction
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileViewModel
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeUiAction
import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeViewModel
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiAction
import com.letthemcook.profile.domain.viewModels.profile.ProfileViewModel
import com.letthemcook.profile.domain.viewModels.settings.SettingsUiAction
import com.letthemcook.profile.domain.viewModels.settings.SettingsViewModel
import com.letthemcook.profile.ui.screens.EditedProfileScreen
import com.letthemcook.profile.ui.screens.PasswordChangeScreen
import com.letthemcook.profile.ui.screens.ProfileScreen
import com.letthemcook.profile.ui.screens.SettingsScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

sealed interface ProfileNavRoutes {
    @Serializable data object EditedProfile : ProfileNavRoutes
    @Serializable data class Profile(val userId: String) : ProfileNavRoutes
    @Serializable data object Settings : ProfileNavRoutes
    @Serializable data object PasswordChange : ProfileNavRoutes
}

fun NavGraphBuilder.addProfileRoutes(
    navController: NavController,
    logOutRoute: Any,
    navBarRoutes: NavBarRoutes,
    onViewRecipe: (String) -> Unit,
    onViewMediaLocal: (File) -> Unit,
    onViewMediaImage: (Bitmap) -> Unit
) {
    composable<ProfileNavRoutes.EditedProfile> {
        val authManager = koinInject<AuthManager>()
        val user = authManager.getUser()

        if (user == null) {
            navController.navigateUp()
            return@composable
        }

        val viewModel = koinViewModel<EditedProfileViewModel>(parameters = { parametersOf(user) })
        val uiState by viewModel.uiState.collectAsState()

        EditedProfileScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    EditedProfileUiAction.NavigateBack -> navController.navigateUp()
                    EditedProfileUiAction.NavigateToHome -> navController.navigate(navBarRoutes.homeRoute)
                    EditedProfileUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    EditedProfileUiAction.NavigateToSettings -> navController.navigate(ProfileNavRoutes.Settings)
                    is EditedProfileUiAction.ViewMediaFile -> onViewMediaLocal(action.file)
                    is EditedProfileUiAction.ViewRecipe -> onViewRecipe(action.recipeId)
                    EditedProfileUiAction.ChangePassword -> navController.navigate(ProfileNavRoutes.PasswordChange)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<ProfileNavRoutes.Profile> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<ProfileNavRoutes.Profile>()
        val userId = route.userId

        val viewModel = koinViewModel<ProfileViewModel>(parameters = { parametersOf(userId) })
        val uiState by viewModel.uiState.collectAsState()

        ProfileScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    ProfileUiAction.NavigateBack -> navController.navigateUp()
                    ProfileUiAction.NavigateToHome -> navController.navigate(navBarRoutes.homeRoute)
                    ProfileUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)

                    is ProfileUiAction.ViewMediaFile -> onViewMediaImage(action.bitmap)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<ProfileNavRoutes.Settings> {
        val viewModel = koinViewModel<SettingsViewModel>()
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

        LaunchedEffect(uiState.passwordChangeResult) {
            if (uiState.passwordChangeResult == PasswordChangeResult.Successful) {
                navController.navigateUp()
            }
        }

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