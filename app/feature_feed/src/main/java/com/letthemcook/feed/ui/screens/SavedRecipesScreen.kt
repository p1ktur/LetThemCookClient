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
import com.letthemcook.feed.domain.viewModels.savedRecipes.SavedRecipesUiAction
import com.letthemcook.feed.domain.viewModels.savedRecipes.SavedRecipesUiState
import com.letthemcook.feed.ui.components.RecipeItem
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer

@Composable
fun SavedRecipesScreen(
    uiState: SavedRecipesUiState,
    onUiAction: (SavedRecipesUiAction) -> Unit
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
                onUiAction(SavedRecipesUiAction.NavigateBack)
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(uiState.savedRecipes, key = { _, it -> it.id }) { index, recipeItemData ->
                RecipeItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    recipeItemData = recipeItemData,
                    onClick = {
                        onUiAction(SavedRecipesUiAction.NavigateToRecipe(recipeItemData.id))
                    }
                )
                if (index != uiState.savedRecipes.lastIndex) {
                    HorizontalDivider(color = LocalAppTheme.current.text)
                }
            }
        }
        NavBar(
            modifier = Modifier.fillMaxWidth(),
            onHomeClick = {},
            onAddClick = {
                onUiAction(SavedRecipesUiAction.NavigateToAddRecipe)
            },
            onProfileClick = {
                onUiAction(SavedRecipesUiAction.NavigateToProfile)
            }
        )
        BottomInsetSpacer()
    }
}