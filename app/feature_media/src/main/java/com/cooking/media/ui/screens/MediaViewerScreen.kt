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
import androidx.compose.material.icons.filled.Close
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
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerForLocalUiAction
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerForLocalUiState
import com.cooking.media.ui.components.ImageViewer
import com.cooking.media.ui.components.VideoViewer
import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun MediaViewerScreen(
    uiState: MediaViewerForLocalUiState,
    onUiAction: (MediaViewerForLocalUiAction) -> Unit
) {
    val blackColor = remember { Color(0xCC111411) }

    var canPlayVideo by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(blackColor)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = {}
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
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
                        onUiAction(MediaViewerForLocalUiAction.Close)
                    }
                    .padding(6.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Close Button",
                tint = Color.White
            )
            Text(
                text = when (uiState.viewedMediaFile) {
                    is MediaFile.Image -> "Viewing Image" //TODO localize
                    is MediaFile.Video -> "Viewing Video" //TODO localize
                    null -> "No Media Found" //TODO localize
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
}