package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.buttons.IconButton
import com.letthemcook.theme.components.dialogs.MediaPickMethodDialog
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.util.UUID

@Composable
fun EditedProfileScreen(
    uiState: EditedProfileUiState,
    onUiAction: (EditedProfileUiAction) -> Unit
) {
    var profilePictureFile: MediaFile.Image? by remember { mutableStateOf(null) }

    val mediaFilePickerManager = koinInject<MediaFilePickerManager>()
    mediaFilePickerManager.RegisterLaunchers()

    LaunchedEffect(uiState.user.profilePictureId) {
        uiState.user.profilePictureId?.let { fileId ->
            withContext(Dispatchers.IO) {
                mediaFilePickerManager.getStoredFile(fileId) {
                    profilePictureFile = it as? MediaFile.Image
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer()
        ToolBar(
            modifier = Modifier.fillMaxWidth(),
            onBackClick = {
                onUiAction(EditedProfileUiAction.NavigateBack)
            },
            onSettingsClick = {
                onUiAction(EditedProfileUiAction.NavigateToSettings)
            }
        )
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
                        if (profilePictureFile == null) {
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
                        } else profilePictureFile?.let {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(LocalAppTheme.current.screenThree)
                                    .clickable {
                                        onUiAction(EditedProfileUiAction.ViewMediaFile(it.file))
                                    },
                                bitmap = it.bitmap.asImageBitmap(),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop
                            )
                        }
                        IconButton(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.BottomEnd),
                            icon = Icons.Outlined.AddAPhoto,
                            onClick = {
                                val fileId = uiState.user.profilePictureId ?: UUID.randomUUID().toString()

                                mediaFilePickerManager.showDialog(
                                    fileType = FileType.IMAGE,
                                    fileId = fileId
                                ) {
                                    profilePictureFile = it as? MediaFile.Image
                                    profilePictureFile?.let { mediaFile ->
                                        onUiAction(EditedProfileUiAction.UpdateProfilePicture(fileId, mediaFile.bitmap))
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
        NavBar(
            modifier = Modifier.fillMaxWidth(),
            onHomeClick = {
                onUiAction(EditedProfileUiAction.NavigateToHome)
            },
            onAddClick = {
                onUiAction(EditedProfileUiAction.NavigateToAddRecipe)
            },
            onProfileClick = {}
        )
        BottomInsetSpacer()
    }

    MediaPickMethodDialog(mediaFilePickerManager)
}