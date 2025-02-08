package com.letthemcook.recipe.ui.navigation

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.recipe.domain.model.SaveStatus
import com.letthemcook.theme.ui.navigation.CookingRoutes
import com.letthemcook.theme.ui.navigation.NavBarRoutes
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeUiAction
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeViewModel
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeUiAction
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeViewModel
import com.letthemcook.recipe.ui.screens.RecipeScreen
import com.letthemcook.recipe.ui.screens.EditedRecipeScreen
import com.letthemcook.theme.ui.navigation.MediaViewerAccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface RecipeNavRoutes {
    @Serializable data class Recipe(val id: String) : RecipeNavRoutes
    @Serializable data class EditedRecipe(val id: String?) : RecipeNavRoutes
}

fun NavGraphBuilder.addRecipeRoutes(
    navController: NavController,
    navBarRoutes: NavBarRoutes,
    cookingRoutes: CookingRoutes,
    mediaViewerAccessState: State<MediaViewerAccess>,
    rawProfileRoute: Any,
    navigateToProfile: (String) -> Unit
) {
    composable<RecipeNavRoutes.Recipe> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<RecipeNavRoutes.Recipe>()
        val recipeId = route.id

        val viewModel = koinViewModel<RecipeViewModel>(parameters = { parametersOf(recipeId) })
        val uiState by viewModel.uiState.collectAsState()

        RecipeScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    RecipeUiAction.NavigateBack -> navController.navigateUp()
                    RecipeUiAction.NavigateToHome -> navBarRoutes.navigateToHome()
                    RecipeUiAction.NavigateToNewRecipe -> navBarRoutes.navigateToNewRecipe()
                    RecipeUiAction.NavigateToProfile -> navBarRoutes.navigateToProfile()
                    is RecipeUiAction.NavigateToOtherProfile -> navigateToProfile(action.userId)
                    RecipeUiAction.EditRecipe -> navController.navigate(RecipeNavRoutes.EditedRecipe(uiState.recipeId))

                    is RecipeUiAction.Cook -> cookingRoutes.navigateToCooking(
                        uiState.ownerId,
                        uiState.recipeId,
                        action.recipeJson,
                        uiState.name
                    )

                    is RecipeUiAction.ViewRecipeBitmap -> mediaViewerAccessState.value.viewBitmap(action.bitmap)
                    is RecipeUiAction.ViewMediaFile -> {
                        val params = RemoteFileManager.RequestParams(
                            userId = uiState.ownerId,
                            fileId = action.file.uid,
                            recipeId = uiState.recipeId,
                            type = action.file.type,
                            isAttachment = true
                        )
                        mediaViewerAccessState.value.viewFile(action.file, params)
                    }

                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<RecipeNavRoutes.EditedRecipe> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<RecipeNavRoutes.EditedRecipe>()
        val recipeId = route.id

        val viewModel = koinViewModel<EditedRecipeViewModel>(parameters = { parametersOf(recipeId) })
        val uiState by viewModel.uiState.collectAsState()

        val coroutineScope = rememberCoroutineScope()

        val newRecipeJson by navBackStackEntry.savedStateHandle
            .getStateFlow<String?>("recipeJson", null)
            .collectAsState()

        val newCookingTime by navBackStackEntry.savedStateHandle
            .getStateFlow<Long?>("cookingTime", null)
            .collectAsState()

        LaunchedEffect(newRecipeJson, newCookingTime) {
            val recipeJson = newRecipeJson

            if (recipeJson != null) {
                if (recipeJson == "null") {
                    viewModel.onUiAction(EditedRecipeUiAction.UpdateRecipeJson(null, null))
                } else {
                    viewModel.onUiAction(EditedRecipeUiAction.UpdateRecipeJson(recipeJson, newCookingTime))
                }
            }
        }

        EditedRecipeScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    EditedRecipeUiAction.NavigateBack -> coroutineScope.launch {
                        while (uiState.saveStatus != SaveStatus.SAVED && isActive) { delay(100L) }
                        navController.navigateUp()
                    }
                    EditedRecipeUiAction.PopToProfile -> coroutineScope.launch {
                        while (uiState.saveStatus != SaveStatus.SAVED && isActive) { delay(100L) }
                        navController.popBackStack(rawProfileRoute, false)
                    }
                    EditedRecipeUiAction.NavigateToHome -> navBarRoutes.navigateToHome()
                    EditedRecipeUiAction.NavigateToNewRecipe -> navBarRoutes.navigateToNewRecipe()
                    EditedRecipeUiAction.NavigateToProfile -> navBarRoutes.navigateToProfile()
                    is EditedRecipeUiAction.NavigateToOtherProfile -> navigateToProfile(action.userId)

                    is EditedRecipeUiAction.EditCooking -> {
                        cookingRoutes.navigateToEditor(
                            uiState.ownerId,
                            uiState.recipeId,
                            action.recipeJson,
                            uiState.name.text.toString(),
                            uiState.productsFilter
                        )
                    }
                    is EditedRecipeUiAction.Cook -> {
                        cookingRoutes.navigateToCooking(
                            uiState.ownerId,
                            uiState.recipeId,
                            uiState.name.text.toString(),
                            action.recipeJson
                        )
                    }

                    is EditedRecipeUiAction.ViewRecipeBitmap -> {
                        mediaViewerAccessState.value.viewBitmap(action.bitmap)
                    }
                    is EditedRecipeUiAction.ViewMediaFile -> {
                        val params = RemoteFileManager.RequestParams(
                            userId = uiState.ownerId,
                            fileId = action.file.uid,
                            recipeId = uiState.recipeId,
                            type = action.file.type,
                            isAttachment = true
                        )
                        mediaViewerAccessState.value.viewFile(action.file, params)
                    }

                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
}