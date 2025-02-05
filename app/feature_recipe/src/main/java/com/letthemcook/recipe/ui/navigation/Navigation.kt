package com.letthemcook.recipe.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letthemcook.theme.ui.navigation.CookingRoutes
import com.letthemcook.theme.ui.navigation.NavBarRoutes
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeUiAction
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeViewModel
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeUiAction
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeViewModel
import com.letthemcook.recipe.ui.screens.RecipeScreen
import com.letthemcook.recipe.ui.screens.EditedRecipeScreen
import com.letthemcook.theme.ui.navigation.MediaViewerAccess
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
    navigateToProfile: (String) -> Unit
) {
    composable<RecipeNavRoutes.Recipe> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<RecipeNavRoutes.Recipe>()
        val recipeId = route.id

        val viewModel = koinViewModel<RecipeViewModel>(parameters = { parametersOf(recipeId) })
        val uiState by viewModel.uiState.collectAsState()
        val reviewsUiState by viewModel.reviewsUiState.collectAsState()

        RecipeScreen(
            uiState = uiState,
            reviewsUiState = reviewsUiState,
            onUiAction = { action ->
                when (action) {
                    RecipeUiAction.NavigateBack -> navController.navigateUp()
                    RecipeUiAction.NavigateToHome -> navBarRoutes.navigateToHome()
                    RecipeUiAction.NavigateToNewRecipe -> navBarRoutes.navigateToNewRecipe()
                    RecipeUiAction.NavigateToProfile -> navBarRoutes.navigateToProfile()
                    is RecipeUiAction.NavigateToOtherProfile -> navigateToProfile(action.userId)

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

        val newRecipeJson by navBackStackEntry.savedStateHandle
            .getStateFlow<String?>("recipeJson", null)
            .collectAsState()

        LaunchedEffect(newRecipeJson) {
            val recipeJson = newRecipeJson

            if (recipeJson != null) {
                viewModel.onUiAction(EditedRecipeUiAction.UpdateRecipeJson(recipeJson))
            }
        }

        EditedRecipeScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    EditedRecipeUiAction.NavigateBack -> navController.navigateUp()
                    EditedRecipeUiAction.NavigateToHome -> navBarRoutes.navigateToHome()
                    EditedRecipeUiAction.NavigateToNewRecipe -> navBarRoutes.navigateToNewRecipe()
                    EditedRecipeUiAction.NavigateToProfile -> navBarRoutes.navigateToProfile()
                    is EditedRecipeUiAction.NavigateToOtherProfile -> navigateToProfile(action.userId)
                    is EditedRecipeUiAction.EditCooking -> cookingRoutes.navigateToEditor(action.recipeJson)
                    is EditedRecipeUiAction.Cook -> cookingRoutes.navigateToCooking(action.recipeJson)

                    is EditedRecipeUiAction.ViewRecipeBitmap -> mediaViewerAccessState.value.viewBitmap(action.bitmap)
                    is EditedRecipeUiAction.ViewMediaFile -> mediaViewerAccessState.value.viewFile(action.file)

                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
}