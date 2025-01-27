package com.letthemcook.editor.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.letthemcook.editor.domain.viewModels.tutorial.TutorialUiAction
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction
import com.letthemcook.editor.domain.viewModels.builder.BuilderViewModel
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiAction
import com.letthemcook.editor.domain.viewModels.cooking.CookingViewModel
import com.letthemcook.editor.ui.screens.BuilderScreen
import com.letthemcook.editor.ui.screens.TutorialScreen
import com.letthemcook.editor.ui.screens.CookingScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface EditorNavRoutes {
    @Serializable data object Builder : EditorNavRoutes
    @Serializable data class Cooking(val cookingData: String) : EditorNavRoutes
    @Serializable data object Tutorial : EditorNavRoutes
}

fun NavGraphBuilder.addEditorRoutes(
    navController: NavController
) {
    composable<EditorNavRoutes.Builder> {
        val viewModel = koinInject<BuilderViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        BuilderScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    BuilderUiAction.NavigateBack -> Unit
                    BuilderUiAction.NavigateToTutorial -> navController.navigate(EditorNavRoutes.Tutorial)
                    BuilderUiAction.TryDemoCooking -> run {
                        val cookingData = viewModel.onUiAction(action) as? String ?: return@run

                        navController.navigate(EditorNavRoutes.Cooking(cookingData))
                        return@run
                    }
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<EditorNavRoutes.Cooking> {
        val viewModel = koinInject<CookingViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        CookingScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    CookingUiAction.NavigateBack -> navController.navigateUp()
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<EditorNavRoutes.Tutorial> {
        TutorialScreen(
            onUiAction = { action ->
                when (action) {
                    TutorialUiAction.NavigateBack -> navController.navigateUp()
                }
            }
        )
    }
}