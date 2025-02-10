package com.letthemcook.feed.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.letthemcook.feed.domain.viewModels.favoredRecipes.FavoredRecipesUiAction
import com.letthemcook.feed.domain.viewModels.favoredRecipes.FavoredRecipesUiState
import com.letthemcook.feed.ui.components.RecipeItem
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@Composable
fun FavoredRecipesScreen(
    uiState: FavoredRecipesUiState,
    onUiAction: (FavoredRecipesUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarBackClick { onUiAction(FavoredRecipesUiAction.NavigateBack) }

            setShowNavigationBar(true)
            setOnNavigateToNewRecipe { onUiAction(FavoredRecipesUiAction.NavigateToNewRecipe) }
            setOnNavigateToProfile { onUiAction(FavoredRecipesUiAction.NavigateToProfile) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(uiState.favoredRecipes, key = { _, it -> it.id }) { index, recipeItemData ->
                RecipeItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    recipeItemData = recipeItemData,
                    index = index,
                    lastIndex = uiState.favoredRecipes.lastIndex,
                    onClick = {
                        onUiAction(FavoredRecipesUiAction.NavigateToRecipe(recipeItemData.id))
                    }
                )
            }
        }
    }
}