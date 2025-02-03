package com.letthemcook.recipe.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.ui.navigation.NavBarRoutes
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeUiAction
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeViewModel
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeUiAction
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeViewModel
import com.letthemcook.recipe.ui.screens.RecipeScreen
import com.letthemcook.recipe.ui.screens.EditedRecipeScreen
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
    editorRoute: Any,
    cookingRoute: Any,
    onViewMedia: (File) -> Unit
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
                    RecipeUiAction.NavigateToHome -> navController.navigate(navBarRoutes.homeRoute)
                    RecipeUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    RecipeUiAction.NavigateToProfile -> navController.navigate(navBarRoutes.profileRoute)
                    is RecipeUiAction.NavigateToOtherProfile -> Unit // TODO implement logic!
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
        val reviewsUiState by viewModel.reviewsUiState.collectAsState()

        EditedRecipeScreen(
            uiState = uiState,
            reviewsUiState = reviewsUiState,
            onUiAction = { action ->
                when (action) {
                    EditedRecipeUiAction.NavigateBack -> navController.navigateUp()
                    EditedRecipeUiAction.NavigateToHome -> navController.navigate(navBarRoutes.homeRoute)
                    EditedRecipeUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    EditedRecipeUiAction.NavigateToProfile -> navController.navigateUp()
                    is EditedRecipeUiAction.NavigateToOtherProfile -> Unit // TODO implement logic!
                    EditedRecipeUiAction.StartEditing -> navController.navigate(editorRoute) // TODO implement logic!

                    is EditedRecipeUiAction.ViewMediaFile -> onViewMedia(action.file)

                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
}