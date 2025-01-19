package com.letthemcook.feed.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letthemcook.feed.domain.viewModels.feed.FeedUiAction
import com.letthemcook.feed.domain.viewModels.feed.FeedUiState
import com.letthemcook.feed.ui.components.RecipeItem
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer

@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onUiAction: (FeedUiAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer()
        ToolBar(
            modifier = Modifier.fillMaxWidth(),
            onSearchClick = {
                onUiAction(FeedUiAction.NavigateToSearch)
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.savedRecipesAmount > 0) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUiAction(FeedUiAction.NavigateToSavedRecipes)
                            }
                            .padding(6.dp)
                            .animateItem(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Saved Recipes: ${uiState.savedRecipesAmount}",
                            style = LocalAppTheme.current.typography.bodyLarge
                        )
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Outlined.Bookmark,
                            contentDescription = "Saved Recipes Icon",
                            tint = LocalAppTheme.current.text
                        )
                    }
                }
            }
            itemsIndexed(uiState.recipes, key = { _, it -> it.id }) { index, recipeItemData ->
                RecipeItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    recipeItemData = recipeItemData,
                    onClick = {
                        onUiAction(FeedUiAction.NavigateToRecipe(recipeItemData.id))
                    }
                )
                if (index != uiState.recipes.lastIndex) {
                    HorizontalDivider(color = LocalAppTheme.current.text)
                }
            }
        }
        NavBar(
            modifier = Modifier.fillMaxWidth(),
            onHomeClick = {},
            onAddClick = {
                onUiAction(FeedUiAction.NavigateToAddRecipe)
            },
            onProfileClick = {
                onUiAction(FeedUiAction.NavigateToProfile)
            }
        )
        BottomInsetSpacer()
    }
}