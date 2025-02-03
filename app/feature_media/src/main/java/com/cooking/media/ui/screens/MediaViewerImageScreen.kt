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
import com.cooking.media.domain.viewModels.mediaViewerImage.MediaViewerImageUiAction
import com.cooking.media.domain.viewModels.mediaViewerImage.MediaViewerImageUiState
import com.cooking.media.ui.components.ImageViewer
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer

@Composable
fun MediaViewerImageScreen(
    uiState: MediaViewerImageUiState,
    onUiAction: (MediaViewerImageUiAction) -> Unit
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
                            onUiAction(MediaViewerImageUiAction.NavigateBack)
                        }
                        .padding(6.dp),
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back Button",
                    tint = Color.White
                )
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
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
        BottomInsetSpacer(blackColor)
    }
}