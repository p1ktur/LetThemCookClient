package com.letthemcook.theme.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.media.MediaFilePickerManager
import com.letthemcook.core.domain.model.data.file.FileType
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.spacers.BottomInsetSpacer

@Composable
fun MediaPickMethodDialog(mediaFilePickerManager: MediaFilePickerManager) {
    val isShown by remember { mediaFilePickerManager.isDialogShown }
    val fileType by remember { mediaFilePickerManager.currentFileType }

    var selectedFileType by remember(fileType) { mutableStateOf(FileType.IMAGE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .run {
                if (isShown) {
                    clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = {
                            mediaFilePickerManager.hideDialog()
                        }
                    )
                } else this
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isShown,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(LocalAppTheme.current.screenOne)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (fileType) {
                    FileType.IMAGE -> {
                        Text(
                            text = "Select Image",
                            style = LocalAppTheme.current.typography.titleSmall
                        )
                    }
                    FileType.VIDEO -> {
                        Text(
                            text = "Select Video",
                            style = LocalAppTheme.current.typography.titleSmall
                        )
                    }
                    FileType.ANY -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedFileType = FileType.IMAGE
                                    }
                                    .padding(6.dp),
                                text = "Select Image",
                                style = LocalAppTheme.current.typography.titleSmall,
                                textDecoration = if (selectedFileType == FileType.IMAGE) TextDecoration.Underline else null
                            )
                            Text(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedFileType = FileType.VIDEO
                                    }
                                    .padding(6.dp),
                                text = "Select Video",
                                style = LocalAppTheme.current.typography.titleSmall,
                                textDecoration = if (selectedFileType == FileType.VIDEO) TextDecoration.Underline else null
                            )
                        }
                    }
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                mediaFilePickerManager.launchGallery(selectedFileType)
                                mediaFilePickerManager.hideDialog()
                            }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .padding(8.dp),
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = "Gallery Icon",
                            tint = LocalAppTheme.current.text
                        )
                        Text(
                            text = "Gallery",
                            style = LocalAppTheme.current.typography.bodyMedium
                        )
                    }
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                mediaFilePickerManager.launchCamera(selectedFileType)
                                mediaFilePickerManager.hideDialog()
                            }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .padding(8.dp),
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Camera Icon",
                            tint = LocalAppTheme.current.text
                        )
                        Text(
                            text = "Camera",
                            style = LocalAppTheme.current.typography.bodyMedium
                        )
                    }
                }
                BottomInsetSpacer(LocalAppTheme.current.screenOne)
            }
        }
    }
}