package com.letthemcook.editor.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letthemcook.editor.domain.viewModels.tutorial.TutorialUiAction
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction
import com.letthemcook.editor.domain.viewModels.builder.BuilderViewModel
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiAction
import com.letthemcook.editor.domain.viewModels.cooking.CookingViewModel
import com.letthemcook.editor.ui.screens.BuilderScreen
import com.letthemcook.editor.ui.screens.TutorialScreen
import com.letthemcook.editor.ui.screens.CookingScreen
import com.letthemcook.theme.ui.navigation.MediaViewerAccess
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface EditorNavRoutes {
    @Serializable data class Builder(val recipeJson: String?) : EditorNavRoutes
    @Serializable data class Cooking(val recipeJson: String) : EditorNavRoutes
    @Serializable data object Tutorial : EditorNavRoutes
}

fun NavGraphBuilder.addEditorRoutes(
    navController: NavController,
    mediaViewerAccessState: State<MediaViewerAccess>
) {
    composable<EditorNavRoutes.Builder> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<EditorNavRoutes.Builder>()
        val recipeJson = route.recipeJson

        val viewModel = koinViewModel<BuilderViewModel>(parameters = { parametersOf(recipeJson) })
        val uiState by viewModel.uiState.collectAsState()

        // TODO revision this
        LaunchedEffect(uiState.viewedMediaFile) {
            uiState.viewedMediaFile?.let { file ->
                mediaViewerAccessState.value.viewFile(file)
                viewModel.onUiAction(BuilderUiAction.StopViewingMediaFile)
            }
        }

        BuilderScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    BuilderUiAction.NavigateBack -> {
                        navController.previousBackStackEntry?.savedStateHandle?.set("recipeJson", viewModel.getRecipeJson())
                        navController.navigateUp()
                    }
                    BuilderUiAction.NavigateToTutorial -> navController.navigate(EditorNavRoutes.Tutorial)
                    BuilderUiAction.TryDemoCooking -> run {
                        val cookingData = viewModel.onUiAction(action) as? String ?: return@run

                        navController.navigate(EditorNavRoutes.Cooking(cookingData))
                        return@run
                    }
                    is BuilderUiAction.ViewMediaFile -> mediaViewerAccessState.value.viewFile(action.file)
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<EditorNavRoutes.Cooking> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<EditorNavRoutes.Cooking>()
        val recipeJson = route.recipeJson

        val viewModel = koinViewModel<CookingViewModel>(parameters = { parametersOf(recipeJson) })
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState.viewedMediaFile) {
            uiState.viewedMediaFile?.let { file ->
                mediaViewerAccessState.value.viewFile(file)
                viewModel.onUiAction(CookingUiAction.StopViewingMediaFile)
            }
        }

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