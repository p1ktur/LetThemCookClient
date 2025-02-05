package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.core.domain.media.MediaFilePickerManager
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileUiAction
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileUiState
import com.letthemcook.profile.ui.components.ProfileEditedData
import com.letthemcook.profile.ui.components.RecipesGrid
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.IconButton
import com.letthemcook.theme.components.dialogs.MediaPickMethodDialog
import com.letthemcook.theme.components.images.ProfileImage
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.util.UUID

@Composable
fun EditedProfileScreen(
    uiState: EditedProfileUiState,
    onUiAction: (EditedProfileUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarBackClick { onUiAction(EditedProfileUiAction.NavigateBack) }
            setOnToolBarSettingsClick { onUiAction(EditedProfileUiAction.NavigateToSettings) }

            setShowNavigationBar(true)
            setOnNavigateToHome { onUiAction(EditedProfileUiAction.NavigateToHome) }
            setOnNavigateToNewRecipe { onUiAction(EditedProfileUiAction.NavigateToNewRecipe) }
        }
    }

    val mediaFilePickerManager = koinInject<MediaFilePickerManager>()
    mediaFilePickerManager.RegisterLaunchers()

    LaunchedEffect(uiState.user.profileBitmapId) {
        uiState.user.profileBitmapId?.let { fileId ->
            withContext(Dispatchers.IO) {
                mediaFilePickerManager.getStoredFile(fileId) {
                    (it as? MediaFile.Image)?.let { mediaFile ->
                        onUiAction(EditedProfileUiAction.UpdateProfileBitmap(fileId, mediaFile.bitmap))
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
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
                    Box {
                        ProfileImage(
                            modifier = Modifier.fillMaxWidth(),
                            bitmap = uiState.userBitmap,
                            onClick = { bitmap ->
                                onUiAction(EditedProfileUiAction.ViewMediaFile(bitmap))
                            }
                        )
                        IconButton(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.BottomEnd),
                            icon = Icons.Outlined.AddAPhoto,
                            onClick = {
                                val fileId = uiState.user.profileBitmapId ?: UUID.randomUUID().toString()
                                mediaFilePickerManager.showDialog(FileType.IMAGE, fileId) {
                                    (it as? MediaFile.Image)?.let { mediaFile ->
                                        onUiAction(EditedProfileUiAction.UpdateProfileBitmap(fileId, mediaFile.bitmap))
                                    }
                                }
                            }
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
                text = uiState.user.login,
                style = LocalAppTheme.current.typography.bodyMedium
            )
            HorizontalDivider(color = LocalAppTheme.current.text)
            ProfileEditedData(
                modifier = Modifier.fillMaxWidth(),
                uiState = uiState,
                onUiAction = onUiAction
            )
            //TODO make choose between published and unpublished
            if (uiState.recipes.isNotEmpty()) {
                HorizontalDivider(color = LocalAppTheme.current.text)
                Text(
                    text = "Your recipes",
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

    MediaPickMethodDialog(mediaFilePickerManager)
}