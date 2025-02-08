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
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.spacers.BottomInsetSpacer

data class AreYouSureDialogConfig(
    val titleText: String,
    val bodyText: String,
    val onOk: () -> Unit,
    val onDismiss: () -> Unit
)

@Composable
fun AreYouSureDialog(config: AreYouSureDialogConfig?) {
    if (config == null) return

    AreYouSureDialog(
        isShown = true,
        titleText = config.titleText,
        bodyText = config.bodyText,
        onOK = config.onOk,
        onDismiss = config.onDismiss
    )
}

@Composable
fun AreYouSureDialog(
    isShown: Boolean,
    titleText: String,
    bodyText: String,
    onOK: () -> Unit,
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
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isShown,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LocalAppTheme.current.screenOne)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = titleText,
                    style = LocalAppTheme.current.typography.titleSmall
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = bodyText,
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        modifier = Modifier.size(140.dp, 40.dp),
                        text = "OK",
                        onClick = onOK
                    )
                    TextButton(
                        modifier = Modifier.size(140.dp, 40.dp),
                        text = "Cancel",
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}