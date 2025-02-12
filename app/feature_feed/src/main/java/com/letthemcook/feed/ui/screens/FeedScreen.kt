package com.letthemcook.feed.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.feed.R
import com.letthemcook.feed.domain.viewModels.feed.FeedUiAction
import com.letthemcook.feed.domain.viewModels.feed.FeedUiState
import com.letthemcook.feed.ui.components.RecipeItem
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onUiAction: (FeedUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarSearchClick { onUiAction(FeedUiAction.NavigateToSearch) }

            setShowNavigationBar(true)
            setOnNavigateToNewRecipe { onUiAction(FeedUiAction.NavigateToAddRecipe) }
            setOnNavigateToProfile { onUiAction(FeedUiAction.NavigateToProfile) }
        }
    }

    val columnLazyListState = rememberLazyListState()

    val isScrolledToBottom by remember {
        derivedStateOf {
            !columnLazyListState.canScrollForward
        }
    }

    LaunchedEffect(isScrolledToBottom) {
        if (isScrolledToBottom && !uiState.loading) {
            onUiAction(FeedUiAction.LoadNextRecipes)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        if (uiState.recipes.isEmpty()) {
            if (uiState.favoredRecipesAmount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onUiAction(FeedUiAction.NavigateToSavedRecipes)
                        }
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(R.string.saved_recipes) + uiState.favoredRecipesAmount.cute(),
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.your_feed_is_empty),
                    style = LocalAppTheme.current.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.try_to_search_new_recipes),
                        style = LocalAppTheme.current.typography.bodyMedium
                    )
                    Icon(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape),
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search Icon",
                        tint = LocalAppTheme.current.text
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.or_follow_other_users),
                        style = LocalAppTheme.current.typography.bodyMedium
                    )
                    Icon(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape),
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "User Icon",
                        tint = LocalAppTheme.current.text
                    )
                }
            }
        } else {
            var isRefreshing by remember { mutableStateOf(false) }

            LaunchedEffect(uiState.loading) {
                if (!uiState.loading && isRefreshing) {
                    isRefreshing = false
                    columnLazyListState.scrollToItem(0)
                }
            }

            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    onUiAction(FeedUiAction.RefreshFeed)
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = columnLazyListState
                ) {
                    if (uiState.favoredRecipesAmount > 0) {
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
                                    text = stringResource(R.string.saved_recipes) + uiState.favoredRecipesAmount.cute(),
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
                            index = index,
                            lastIndex = uiState.recipes.lastIndex,
                            onClick = {
                                onUiAction(FeedUiAction.NavigateToRecipe(recipeItemData.id))
                            },
                            onUserLoginClick = {
                                onUiAction(FeedUiAction.NavigateToUser(recipeItemData.ownerId))
                            }
                        )
                    }
                }
            }
        }
    }
}