package com.letthemcook.feed.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.letthemcook.core.ui.navigation.NavBarRoutes
import com.letthemcook.feed.domain.viewModels.savedRecipes.SavedRecipesUiAction
import com.letthemcook.feed.domain.viewModels.savedRecipes.SavedRecipesViewModel
import com.letthemcook.feed.domain.viewModels.feed.FeedUiAction
import com.letthemcook.feed.domain.viewModels.feed.FeedViewModel
import com.letthemcook.feed.domain.viewModels.search.SearchUiAction
import com.letthemcook.feed.domain.viewModels.search.SearchViewModel
import com.letthemcook.feed.ui.screens.FeedScreen
import com.letthemcook.feed.ui.screens.SavedRecipesScreen
import com.letthemcook.feed.ui.screens.SearchScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface FeedNavRoutes {
    @Serializable data object Feed : FeedNavRoutes
    @Serializable data object Search : FeedNavRoutes
    @Serializable data object SavedRecipes : FeedNavRoutes
}

fun NavGraphBuilder.addFeedRoutes(
    navController: NavController,
    navBarRoutes: NavBarRoutes
) {
    composable<FeedNavRoutes.Feed> {
        val viewModel = koinInject<FeedViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        FeedScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    FeedUiAction.NavigateToProfile -> navController.navigate(navBarRoutes.profileRoute)
                    FeedUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    FeedUiAction.NavigateToSearch -> navController.navigate(FeedNavRoutes.Search)
                    FeedUiAction.NavigateToSavedRecipes -> navController.navigate(FeedNavRoutes.SavedRecipes)
                    is FeedUiAction.NavigateToRecipe -> Unit //TODO recipe route!!
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<FeedNavRoutes.Search> {
        val viewModel = koinInject<SearchViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        SearchScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    SearchUiAction.NavigateBack -> navController.navigateUp()
                    is SearchUiAction.NavigateToRecipe -> Unit //TODO recipe route!!
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<FeedNavRoutes.SavedRecipes> {
        val viewModel = koinInject<SavedRecipesViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        SavedRecipesScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    SavedRecipesUiAction.NavigateBack -> navController.navigateUp()
                    SavedRecipesUiAction.NavigateToFeed -> navController.navigate(navBarRoutes.homeRoute)
                    SavedRecipesUiAction.NavigateToProfile -> navController.navigate(navBarRoutes.profileRoute)
                    SavedRecipesUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    is SavedRecipesUiAction.NavigateToRecipe -> Unit //TODO recipe route!!
                }
                viewModel.onUiAction(action)
            }
        )
    }
}