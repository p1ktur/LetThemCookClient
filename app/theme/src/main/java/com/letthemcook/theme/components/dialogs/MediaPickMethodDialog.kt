package com.letthemcook.theme.components.dialogs

import android.graphics.Paint.Align
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.spacers.BottomInsetSpacer

@Composable
fun MediaPickMethodDialog(
    isShown: Boolean,
    onGalleryOptionSelected: () -> Unit,
    onCameraOptionSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .run {
                if (isShown) {
                    clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = onDismiss
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
                Text(
                    text = "Select Image",
                    style = LocalAppTheme.current.typography.titleSmall
                )
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onGalleryOptionSelected)
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
                            .clickable(onClick = onCameraOptionSelected)
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