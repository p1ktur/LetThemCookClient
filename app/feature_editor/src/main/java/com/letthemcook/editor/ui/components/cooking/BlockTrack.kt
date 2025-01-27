package com.letthemcook.editor.ui.components.cooking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.list.forEachReversed
import com.letthemcook.editor.domain.cooking.BlockCookingState
import com.letthemcook.editor.domain.cooking.track.EmptyTrackData
import com.letthemcook.editor.domain.cooking.track.ParallelTrackData
import com.letthemcook.editor.domain.cooking.track.SingleTrackData
import com.letthemcook.editor.domain.cooking.track.TrackData
import com.letthemcook.editor.domain.cooking.track.TrackDataOptions
import com.letthemcook.editor.domain.cooking.track.getOptions
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.ui.drawing.getBlockBodyBrush
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun BlockTrack(
    modifier: Modifier = Modifier,
    trackData: TrackData,
    trackDataCounter: Int,
    onPreviousClick: (BlockComponent) -> Unit,
    onBlockClick: (BlockComponent) -> Unit,
    onNextClick: (BlockComponent) -> Unit
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        when (trackData) {
            EmptyTrackData -> return
            is ParallelTrackData -> {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(vertical = 4.dp)
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(LocalAppTheme.current.text, RoundedCornerShape(4.dp))
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trackData.children.forEachReversed { childTrackData ->
                        BlockTrack(
                            trackData = childTrackData,
                            trackDataCounter = trackDataCounter,
                            onPreviousClick = onPreviousClick,
                            onBlockClick = onBlockClick,
                            onNextClick = onNextClick
                        )
                    }
                }
            }
            is SingleTrackData -> key(trackData.hashCode()) {
                val (firstOption, secondOption) = trackData.getOptions()

                val firstImageVector = when (firstOption) {
                    TrackDataOptions.NONE -> null
                    TrackDataOptions.GO_TO_PREVIOUS -> Icons.AutoMirrored.Filled.ArrowBack
                    TrackDataOptions.GO_TO_NEXT -> Icons.AutoMirrored.Filled.ArrowForward
                    TrackDataOptions.FINISH -> Icons.Default.Done
                    TrackDataOptions.WAITING -> null
                }
                val secondImageVector = when (secondOption) {
                    TrackDataOptions.NONE -> null
                    TrackDataOptions.GO_TO_PREVIOUS -> Icons.AutoMirrored.Filled.ArrowBack
                    TrackDataOptions.GO_TO_NEXT -> Icons.AutoMirrored.Filled.ArrowForward
                    TrackDataOptions.FINISH -> Icons.Default.Done
                    TrackDataOptions.WAITING -> null
                }

                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (firstOption == TrackDataOptions.WAITING) {
                        SmallLoadingIndicator()
                    } else firstImageVector?.let { imageVector ->
                        SmallIconButton(
                            imageVector = imageVector,
                            onClick = {
                                onPreviousClick(trackData.ref)
                            }
                        )
                    }
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onBlockClick(trackData.ref)
                            }
                            .border(
                                2.dp,
                                getBlockBodyBrush(
                                    LocalAppTheme.current.background,
                                    trackData.ref.colorOption.color
                                ),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(4.dp),
                        text = when (trackData.ref.cookingState) {
                            BlockCookingState.COOKING -> trackData.ref.name
                            else -> trackData.ref.cookingState.toString()
                        },
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    if (secondOption == TrackDataOptions.WAITING) {
                        SmallLoadingIndicator()
                    } else secondImageVector?.let { imageVector ->
                        SmallIconButton(
                            imageVector = imageVector,
                            onClick = {
                                onNextClick(trackData.ref)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallLoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(24.dp)
            .padding(2.dp),
        color = LocalAppTheme.current.text,
        strokeWidth = 2.dp
    )
}

@Composable
private fun SmallIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    Icon(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(2.dp),
        imageVector = imageVector,
        contentDescription = "Small Icon Button",
        tint = LocalAppTheme.current.text
    )
}