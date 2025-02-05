package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiAction
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiState
import com.letthemcook.profile.ui.components.RecipesGrid
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.images.ProfileImage
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
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

    if (uiState.user == null) {
        LoadingScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                        text = uiState.user.totalRecipes.toString(),
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Text(
                        text = "Recipes",
                        style = LocalAppTheme.current.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = uiState.user.averageRating.cute(),
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Text(
                        text = "Average Rating",
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
                        text = uiState.user.totalPreparations.toString(),
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Text(
                        text = "Preparations",
                        style = LocalAppTheme.current.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = uiState.user.totalFollowers.toString(),
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Text(
                        text = "Followers",
                        style = LocalAppTheme.current.typography.bodySmall
                    )
                }
            }
            Text(
                text = "@${uiState.user.login}",
                style = LocalAppTheme.current.typography.bodyMedium
            )
            TextButton(
                modifier = Modifier,
                text = if (uiState.user.isFollowed) "Unfollow" else "Follow",
                onClick = {
                    onUiAction(ProfileUiAction.FollowOrUnfollow)
                }
            )
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
            if (uiState.recipes.isNotEmpty()) {
                HorizontalDivider(color = LocalAppTheme.current.text)
                Text(
                    text = "@${uiState.user.login}'s recipes",
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                RecipesGrid(
                    modifier = Modifier.fillMaxWidth(),
                    recipes = uiState.recipes,
                    onRecipeClick = { recipe ->
                        // TODO show recipe page
                    }
                )
            }
        }
    }
}