package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiAction
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiState
import com.letthemcook.profile.ui.components.RecipesGrid
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import com.letthemcook.theme.ui.screens.LoadingScreen
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onUiAction: (ProfileUiAction) -> Unit
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
                onUiAction(ProfileUiAction.NavigateBack)
            }
        )
        if (uiState.user == null) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
                        if (uiState.userImage == null) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(LocalAppTheme.current.screenThree),
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.FillWidth,
                                colorFilter = ColorFilter.tint(LocalAppTheme.current.text, BlendMode.SrcAtop)
                            )
                        } else {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(LocalAppTheme.current.screenThree)
                                    .clickable {
                                        onUiAction(ProfileUiAction.ViewMediaFile(uiState.userImage))
                                    },
                                bitmap = uiState.userImage.asImageBitmap(),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop
                            )
                        }
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
        NavBar(
            modifier = Modifier.fillMaxWidth(),
            onHomeClick = {
                onUiAction(ProfileUiAction.NavigateToHome)
            },
            onAddClick = {
                onUiAction(ProfileUiAction.NavigateToAddRecipe)
            },
            onProfileClick = {
                onUiAction(ProfileUiAction.NavigateToEditedProfile)
            }
        )
        BottomInsetSpacer()
    }
}