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
import com.letthemcook.core.domain.media.MediaFilePickerManager
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.core.domain.model.file.getProfilePictureFileName
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiAction
import com.letthemcook.profile.domain.viewModels.profile.ProfileUiState
import com.letthemcook.profile.ui.components.ProfileEditedData
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

// TODO separate page for this user and other user

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onUiAction: (ProfileUiAction) -> Unit
) {
    val profilePictureName = getProfilePictureFileName()
    var profilePictureFile: MediaFile.Image? by remember { mutableStateOf(null) }

    val mediaFilePickerManager = koinInject<MediaFilePickerManager>()
    mediaFilePickerManager.RegisterLaunchers()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            mediaFilePickerManager.getStoredFile(profilePictureName) {
                profilePictureFile = it as? MediaFile.Image
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
                onUiAction(ProfileUiAction.NavigateBack)
            },
            onSettingsClick = {
                onUiAction(ProfileUiAction.NavigateToSettings)
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
                        text = "999",
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Text(
                        text = "Recipes",
                        style = LocalAppTheme.current.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "4.50",
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
                                        onUiAction(ProfileUiAction.ViewMediaFile(it.file))
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
                                mediaFilePickerManager.showDialog(FileType.IMAGE, profilePictureName) {
                                    profilePictureFile = it as? MediaFile.Image
                                }
                                // TODO update photo and save and upload
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
                        text = "999",
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Text(
                        text = "Preparations",
                        style = LocalAppTheme.current.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "9999",
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Text(
                        text = "Followers",
                        style = LocalAppTheme.current.typography.bodySmall
                    )
                }
            }
            Text(
                text = "@username",
                style = LocalAppTheme.current.typography.bodyMedium
            )
            HorizontalDivider(color = LocalAppTheme.current.text)
            ProfileEditedData(
                modifier = Modifier.fillMaxWidth(),
                uiState = uiState,
                onUiAction = onUiAction
            )
            HorizontalDivider(color = LocalAppTheme.current.text)
            Text(
                text = "Your recipes",
                style = LocalAppTheme.current.typography.bodyLarge
            )
            //TODO display recipes
        }
        NavBar(
            modifier = Modifier.fillMaxWidth(),
            onHomeClick = {
                onUiAction(ProfileUiAction.NavigateToHome)
            },
            onAddClick = {
                onUiAction(ProfileUiAction.NavigateToAddRecipe)
            },
            onProfileClick = {}
        )
        BottomInsetSpacer()
    }

    MediaPickMethodDialog(mediaFilePickerManager)
}