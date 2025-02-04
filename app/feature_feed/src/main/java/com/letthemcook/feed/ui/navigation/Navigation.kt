package com.letthemcook.feed.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.letthemcook.core.ui.navigation.NavBarRoutes
import com.letthemcook.feed.domain.viewModels.favoredRecipes.FavoredRecipesUiAction
import com.letthemcook.feed.domain.viewModels.favoredRecipes.FavoredRecipesViewModel
import com.letthemcook.feed.domain.viewModels.feed.FeedUiAction
import com.letthemcook.feed.domain.viewModels.feed.FeedViewModel
import com.letthemcook.feed.domain.viewModels.search.SearchUiAction
import com.letthemcook.feed.domain.viewModels.search.SearchViewModel
import com.letthemcook.feed.ui.screens.FeedScreen
import com.letthemcook.feed.ui.screens.FavoredRecipesScreen
import com.letthemcook.feed.ui.screens.SearchScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface FeedNavRoutes {
    @Serializable data object Feed : FeedNavRoutes
    @Serializable data object Search : FeedNavRoutes
    @Serializable data object FavoredRecipes : FeedNavRoutes
}

fun NavGraphBuilder.addFeedRoutes(
    navController: NavController,
    navBarRoutes: NavBarRoutes
) {
    composable<FeedNavRoutes.Feed> {
        val viewModel = koinViewModel<FeedViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        FeedScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    FeedUiAction.NavigateToProfile -> navController.navigate(navBarRoutes.profileRoute)
                    FeedUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    FeedUiAction.NavigateToSearch -> navController.navigate(FeedNavRoutes.Search)
                    FeedUiAction.NavigateToSavedRecipes -> navController.navigate(FeedNavRoutes.FavoredRecipes)
                    is FeedUiAction.NavigateToRecipe -> Unit //TODO recipe route!!
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<FeedNavRoutes.Search> {
        val viewModel = koinViewModel<SearchViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        SearchScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    SearchUiAction.NavigateBack -> navController.navigateUp()
                    is SearchUiAction.NavigateToRecipe -> Unit //TODO recipe route!!
                    is SearchUiAction.NavigateToUser -> Unit //TODO user route!!
                    else -> Unit
                }
                viewModel.onUiAction(action)
            }
        )
    }
    composable<FeedNavRoutes.FavoredRecipes> {
        val viewModel = koinViewModel<FavoredRecipesViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        FavoredRecipesScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    FavoredRecipesUiAction.NavigateBack -> navController.navigateUp()
                    FavoredRecipesUiAction.NavigateToFeed -> navController.navigate(navBarRoutes.homeRoute)
                    FavoredRecipesUiAction.NavigateToProfile -> navController.navigate(navBarRoutes.profileRoute)
                    FavoredRecipesUiAction.NavigateToAddRecipe -> navController.navigate(navBarRoutes.addRoute)
                    is FavoredRecipesUiAction.NavigateToRecipe -> Unit //TODO recipe route!!
                }
                viewModel.onUiAction(action)
            }
        )
    }
}