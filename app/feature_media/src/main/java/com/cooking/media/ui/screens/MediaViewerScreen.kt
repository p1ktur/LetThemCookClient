package com.cooking.media.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.data.file.MediaFile
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerUiAction
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerUiState
import com.cooking.media.ui.components.ImageViewer
import com.cooking.media.ui.components.VideoViewer
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer

@Composable
fun MediaViewerScreen(
    uiState: MediaViewerUiState,
    onUiAction: (MediaViewerUiAction) -> Unit
) {
    val blackColor = remember { Color(0xFF111811) }

    var canPlayVideo by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(blackColor)
    ) {
        TopInsetSpacer(blackColor)
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            canPlayVideo = false
                            onUiAction(MediaViewerUiAction.NavigateBack)
                        }
                        .padding(6.dp),
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back Button",
                    tint = Color.White
                )
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = when (uiState.viewedMediaFile) {
                        is MediaFile.Image -> "Viewing Image" //TODO localize
                        is MediaFile.Video -> "Viewing Video" //TODO localize
                        null -> ""
                    },
                    style = LocalAppTheme.current.typography.titleSmall,
                    color = Color.White
                )
            }
            when (val mediaFile = uiState.viewedMediaFile) {
                is MediaFile.Image -> {
                    ImageViewer(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = mediaFile.bitmap
                    )
                }
                is MediaFile.Video -> {
                    VideoViewer(
                        modifier = Modifier.fillMaxSize(),
                        canPlay = canPlayVideo,
                        uri = mediaFile.file.uri
                    )
                }
                null -> Unit
            }
        }
        BottomInsetSpacer(blackColor)
    }
}