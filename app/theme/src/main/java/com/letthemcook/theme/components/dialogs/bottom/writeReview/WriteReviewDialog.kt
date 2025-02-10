package com.letthemcook.theme.components.dialogs.bottom.writeReview

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.R
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.textFields.MultiLineTextField

@Composable
fun WriteReviewDialog(reviewWriter: ReviewWriter) {
    val isShown by remember { reviewWriter.isDialogShown }
    val showEmptyTextError by remember { reviewWriter.showEmptyTextError }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .run {
                if (isShown) {
                    clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = reviewWriter::hideDialog
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
                if (showEmptyTextError) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp),
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error Show Icon",
                            tint = LocalAppTheme.current.errorText
                        )
                        Text(
                            text = stringResource(R.string.review_text_cannot_be_empty),
                            style = LocalAppTheme.current.typography.bodySmall,
                            color = LocalAppTheme.current.errorText
                        )
                    }
                }
                MultiLineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = reviewWriter.reviewTextState,
                    labelText = stringResource(R.string.review),
                    placeholderText = stringResource(R.string.type_review),
                    onSendButtonClick = {
                        reviewWriter.hideDialogAndSendReview()
                    },
                    backgroundColor = LocalAppTheme.current.screenOne
                )
                BottomInsetSpacer(LocalAppTheme.current.screenOne)
            }
        }
    }
}