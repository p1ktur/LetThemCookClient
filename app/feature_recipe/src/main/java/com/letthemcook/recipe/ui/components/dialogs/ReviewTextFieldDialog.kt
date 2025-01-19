package com.letthemcook.recipe.ui.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.input.TextFieldState
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
import com.letthemcook.theme.components.textFields.MultiLineTextField
import com.letthemcook.theme.components.textFields.SingleLineTextField

@Composable
fun ReviewTextFieldDialog(
    isShown: Boolean,
    reviewState: TextFieldState,
    onSendReview: () -> Unit,
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
                    .padding(16.dp)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MultiLineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = reviewState,
                    labelText = "Write review",
                    onSendButtonClick = onSendReview
                )
                BottomInsetSpacer(LocalAppTheme.current.screenOne)
            }
        }
    }
}