package com.letthemcook.editor.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.domain.model.remote.WeightedProduct
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
import kotlin.reflect.typeOf

sealed interface EditorNavRoutes {
    @Serializable
    data class Builder(
        val ownerId: String,
        val recipeId: String,
        val recipeJson: String?,
        val recipeName: String,
        val products: List<WeightedProduct>
    ) : EditorNavRoutes
    @Serializable
    data class Cooking(
        val ownerId: String,
        val recipeId: String,
        val recipeJson: String,
        val recipeName: String
    ) : EditorNavRoutes
    @Serializable data object Tutorial : EditorNavRoutes
}

fun NavGraphBuilder.addEditorRoutes(
    navController: NavController,
    mediaViewerAccessState: State<MediaViewerAccess>
) {
    composable<EditorNavRoutes.Builder>(
        typeMap = mapOf(
            typeOf<List<WeightedProduct>>() to WeightedProductsListNavType
        )
    ) { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<EditorNavRoutes.Builder>()
        val ownerId = route.ownerId
        val recipeId = route.recipeId
        val recipeJson = route.recipeJson
        val recipeName = route.recipeName
        val products = route.products

        val viewModel = koinViewModel<BuilderViewModel>(parameters = {
            parametersOf(ownerId, recipeId, recipeJson, recipeName, products)
        })
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.viewMediaFile = { blockId, file ->
                val params = RemoteFileManager.RequestParams(
                    userId = uiState.ownerId,
                    fileId = file.uid,
                    recipeId = uiState.recipeId,
                    blockId = blockId,
                    type = file.type
                )

                mediaViewerAccessState.value.viewFile(file, params)
            }
        }

        BuilderScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    BuilderUiAction.NavigateBack -> {
                        navController.previousBackStackEntry?.savedStateHandle?.set("recipeJson", viewModel.getRecipeJson())
                        navController.previousBackStackEntry?.savedStateHandle?.set("cookingTime", viewModel.getCookingTime())
                        navController.navigateUp()
                    }
                    BuilderUiAction.NavigateToTutorial -> navController.navigate(EditorNavRoutes.Tutorial)
                    BuilderUiAction.TryDemoCooking -> run {
                        val cookingData = viewModel.prepareCookingData() ?: return@run
                        navController.navigate(EditorNavRoutes.Cooking(uiState.ownerId, uiState.recipeId, cookingData, uiState.recipeName))
                    }
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<EditorNavRoutes.Cooking> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<EditorNavRoutes.Cooking>()
        val ownerId = route.ownerId
        val recipeId = route.recipeId
        val recipeJson = route.recipeJson
        val recipeName = route.recipeName

        val viewModel = koinViewModel<CookingViewModel>(parameters = {
            parametersOf(ownerId, recipeId, recipeJson, recipeName)
        })
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.viewMediaFile = { blockId, file ->
                val params = RemoteFileManager.RequestParams(
                    userId = uiState.ownerId,
                    fileId = file.uid,
                    recipeId = uiState.recipeId,
                    blockId = blockId,
                    type = file.type
                )

                mediaViewerAccessState.value.viewFile(file, params)
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