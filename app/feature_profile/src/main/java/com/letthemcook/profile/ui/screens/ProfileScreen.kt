package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.profile.R
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiAction
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiState
import com.letthemcook.profile.ui.components.recipesGrid
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.images.ProfileImage
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import com.letthemcook.theme.ui.screens.LoadingScreen

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onUiAction: (ProfileUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarBackClick { onUiAction(ProfileUiAction.NavigateBack) }

            setShowNavigationBar(true)
            setOnNavigateToHome { onUiAction(ProfileUiAction.NavigateToHome) }
            setOnNavigateToNewRecipe { onUiAction(ProfileUiAction.NavigateToNewRecipe) }
            setOnNavigateToProfile { onUiAction(ProfileUiAction.NavigateToEditedProfile) }
        }
    }

    val recipesByThree by remember(uiState.recipes) {
        derivedStateOf {
            val recipes = uiState.recipes
            val recipesByThree = mutableListOf<Array<RecipeItemData?>>()
            var currentIndex = 0

            while (currentIndex <= recipes.lastIndex) {
                val first = recipes[currentIndex]
                val second = recipes.getOrNull(currentIndex + 1)
                val third = recipes.getOrNull(currentIndex + 2)

                val array = arrayOf(first, second, third)

                recipesByThree.add(array)

                currentIndex += 3
            }

            recipesByThree
        }
    }

    val lazyColumnState = rememberLazyListState()

    val isScrolledToBottom by remember {
        derivedStateOf {
            !lazyColumnState.canScrollForward && lazyColumnState.canScrollBackward
        }
    }

    LaunchedEffect(isScrolledToBottom) {
        if (isScrolledToBottom && !uiState.loadingRecipes && uiState.recipes.isNotEmpty()) {
            onUiAction(ProfileUiAction.LoadRecipes)
        }
    }

    if (uiState.user == null) {
        LoadingScreen()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(0.3f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.user.totalRecipes.cute(),
                                style = LocalAppTheme.current.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.recipes),
                                style = LocalAppTheme.current.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = uiState.user.averageRating.cute(),
                                style = LocalAppTheme.current.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.average_rating),
                                style = LocalAppTheme.current.typography.bodySmall
                            )
                        }
                        Column(
                            modifier = Modifier.weight(0.4f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ProfileImage(
                                modifier = Modifier.fillMaxWidth(),
                                bitmap = uiState.userBitmap,
                                onClick = { bitmap ->
                                    onUiAction(ProfileUiAction.ViewMediaFile(bitmap))
                                }
                            )
                        }
                        Column(
                            modifier = Modifier.weight(0.3f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.user.totalPreparations.cute(),
                                style = LocalAppTheme.current.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.preparations),
                                style = LocalAppTheme.current.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = uiState.user.totalFollowers.cute(),
                                style = LocalAppTheme.current.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.followers),
                                style = LocalAppTheme.current.typography.bodySmall
                            )
                        }
                    }
                    Text(
                        text = "@${uiState.user.login}",
                        style = LocalAppTheme.current.typography.bodyMedium
                    )
                    if (!uiState.isSelf) {
                        TextButton(
                            modifier = Modifier,
                            text = if (uiState.user.isFollowed) {
                                stringResource(R.string.unfollow)
                            } else {
                                stringResource(R.string.follow)
                            },
                            onClick = {
                                onUiAction(ProfileUiAction.FollowOrUnfollow)
                            }
                        )
                    }
                    if (uiState.user.name != null || uiState.user.surname != null) {
                        Text(
                            text = "${uiState.user.name} ${uiState.user.surname}",
                            style = LocalAppTheme.current.typography.bodySmall
                        )
                    }
                    if (uiState.user.birthDate != null) {
                        Text(
                            text = "${uiState.user.birthDateString()}",
                            style = LocalAppTheme.current.typography.bodySmall
                        )
                    }
                    if (uiState.user.about != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "${uiState.user.about}",
                            style = LocalAppTheme.current.typography.bodySmall
                        )
                    }
                }
            }
            if (uiState.recipes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = LocalAppTheme.current.text)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "@${uiState.user.login}" + stringResource(R.string.s_recipes),
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                }
            }
            recipesGrid(
                recipesByThree = recipesByThree,
                onRecipeClick = { recipe ->
                    onUiAction(ProfileUiAction.NavigateToRecipe(recipe.id))
                }
            )
        }
    }
}