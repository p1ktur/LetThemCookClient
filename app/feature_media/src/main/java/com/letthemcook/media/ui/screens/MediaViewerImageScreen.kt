package com.letthemcook.media.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.letthemcook.media.domain.viewModels.mediaViewerImage.MediaViewerImageUiAction
import com.letthemcook.media.domain.viewModels.mediaViewerImage.MediaViewerImageUiState
import com.letthemcook.media.ui.components.ImageViewer
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun MediaViewerImageScreen(
    uiState: MediaViewerImageUiState,
    onUiAction: (MediaViewerImageUiAction) -> Unit
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
                        onUiAction(MediaViewerImageUiAction.Close)
                    }
                    .padding(6.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Close Button",
                tint = Color.White
            )
            Text(
                text = "Viewing Image", // TODO localize
                style = LocalAppTheme.current.typography.titleSmall,
                color = Color.White
            )
        }
        ImageViewer(
            modifier = Modifier.fillMaxSize(),
            bitmap = uiState.bitmap
        )
    }
}