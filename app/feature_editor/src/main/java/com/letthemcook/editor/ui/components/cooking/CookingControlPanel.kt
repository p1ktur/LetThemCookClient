package com.letthemcook.editor.ui.components.cooking

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.isLongTime
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.format.toTimeString
import com.letthemcook.editor.domain.cooking.CookingState
import com.letthemcook.editor.domain.cooking.track.EmptyTrackData
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiAction
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiState
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.ProgressBar

@Composable
fun CookingControlPanel(
    modifier: Modifier = Modifier,
    uiState: CookingUiState,
    onUiAction: (CookingUiAction) -> Unit
) {
    val context = LocalContext.current

    val totalTimeText = remember(uiState.totalCookingTime) {
        uiState.totalCookingTime.toShortTimeString(context, canInstant = false)
    }

    val leftTimeText = remember(uiState.totalCookingTime, uiState.cookingTimeLeft) {
        if (uiState.totalCookingTime.isLongTime()) {
            uiState.cookingTimeLeft.toTimeString(context, canInstant = false)
        } else {
            uiState.cookingTimeLeft.toShortTimeString(context, canInstant = false)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalAppTheme.current.background)
            .verticalScroll(rememberScrollState())
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "$leftTimeText / $totalTimeText",
                style = LocalAppTheme.current.typography.bodyMedium
            )
            if (uiState.cookingProgress < 1f) {
                when (uiState.cookingState) {
                    CookingState.NOT_STARTED, CookingState.PAUSED -> {
                        Icon(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onUiAction(CookingUiAction.StartCooking)
                                }
                                .padding(4.dp)
                                .align(Alignment.Center),
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Button",
                            tint = LocalAppTheme.current.text
                        )
                    }
                    CookingState.STARTED -> {
                        Icon(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onUiAction(CookingUiAction.PauseCooking)
                                }
                                .padding(4.dp)
                                .align(Alignment.Center),
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause Button",
                            tint = LocalAppTheme.current.text
                        )
                    }
                }
            }
            if (uiState.cookingState != CookingState.NOT_STARTED) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            onUiAction(CookingUiAction.StopCooking)
                        }
                        .padding(4.dp)
                        .align(Alignment.CenterEnd),
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Button",
                    tint = LocalAppTheme.current.text
                )
            }
        }
        HorizontalDivider(color = LocalAppTheme.current.text)
        ProgressBar(
            progress = uiState.cookingProgress,
            height = 4.dp
        )
        if (uiState.trackData != EmptyTrackData) {
            HorizontalDivider(color = LocalAppTheme.current.text)
            BlockTrack(
                modifier = Modifier
                    .padding(vertical = if (uiState.trackData == EmptyTrackData) 0.dp else 4.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .animateContentSize(),
                trackData = uiState.trackData,
                trackDataCounter = uiState.trackDataCounter,
                onPreviousClick = { ref ->
                    onUiAction(CookingUiAction.RestoreBlock(ref))
                },
                onBlockClick = { ref ->
                    onUiAction(CookingUiAction.SelectBlock(ref))
                },
                onNextClick = { ref ->
                    onUiAction(CookingUiAction.FinishBlock(ref))
                }
            )
        }
        HorizontalDivider(color = LocalAppTheme.current.text)
        uiState.selectedBlock?.let { block ->
            BlockInfo(
                selectedBlock = block,
                canvasCounter = uiState.canvasCounter,
                onUiAction = onUiAction
            )
            HorizontalDivider(color = LocalAppTheme.current.text)
        }
    }
}