package com.letthemcook.feed.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letthemcook.feed.domain.viewModels.favoredRecipes.FavoredRecipesUiAction
import com.letthemcook.feed.domain.viewModels.favoredRecipes.FavoredRecipesUiState
import com.letthemcook.feed.ui.components.RecipeItem
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer

@Composable
fun FavoredRecipesScreen(
    uiState: FavoredRecipesUiState,
    onUiAction: (FavoredRecipesUiAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer()
        ToolBar(
            modifier = Modifier.fillMaxWidth(),
            onBackClick = {
                onUiAction(FavoredRecipesUiAction.NavigateBack)
            }
        )
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
                    onClick = {
                        onUiAction(FavoredRecipesUiAction.NavigateToRecipe(recipeItemData.id))
                    }
                )
                if (index != uiState.favoredRecipes.lastIndex) {
                    HorizontalDivider(color = LocalAppTheme.current.text)
                }
            }
        }
        NavBar(
            modifier = Modifier.fillMaxWidth(),
            onHomeClick = {},
            onAddClick = {
                onUiAction(FavoredRecipesUiAction.NavigateToAddRecipe)
            },
            onProfileClick = {
                onUiAction(FavoredRecipesUiAction.NavigateToProfile)
            }
        )
        BottomInsetSpacer()
    }
}