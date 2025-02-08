package com.letthemcook.media.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.letthemcook.core.domain.format.isLongTime
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.format.toTimeString
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.ProgressBar
import com.letthemcook.theme.components.buttons.IconButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToLong

private enum class VideoStatus {
    PLAYING,
    PAUSED,
    STOPPED
}

@Composable
fun VideoViewer(
    modifier: Modifier = Modifier,
    canPlay: Boolean,
    uri: Uri
) {
    val context = LocalContext.current
    val blackColor = remember { Color(0xCC111411) }

    var showUi by remember { mutableStateOf(true) }
    val playerAlpha by animateFloatAsState(
        targetValue = if (canPlay) 1f else 0f
    )

    val exoPlayer = remember(uri) {
        val exoPlayer = ExoPlayer.Builder(context).build()

        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        exoPlayer
    }

    var videoStatus by remember { mutableStateOf(VideoStatus.PAUSED) }
    var playerProgress by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }

    val totalTimeText = remember(totalDuration) {
        totalDuration.toShortTimeString(canInstant = false)
    }

    val leftTimeText = remember(playerProgress, totalDuration) {
        if (totalDuration.isLongTime()) {
            playerProgress.toTimeString(canInstant = false)
        } else {
            playerProgress.toShortTimeString(canInstant = false)
        }
    }

    LaunchedEffect(Unit) {
        delay(150)
        showUi = false
    }

    LaunchedEffect(canPlay) {
        if (!canPlay) {
            exoPlayer.playWhenReady = false
            videoStatus = VideoStatus.PAUSED
        }
    }

    LaunchedEffect(exoPlayer, videoStatus) {
        while (isActive && videoStatus == VideoStatus.PLAYING) {
            playerProgress = exoPlayer.currentPosition
            totalDuration = exoPlayer.duration.coerceAtLeast(1L)

            if (playerProgress >= totalDuration) {
                videoStatus = VideoStatus.STOPPED
            } else {
                delay(100)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(playerAlpha)
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = {
                        showUi = !showUi
                    }
                ),
            factory = {
                PlayerView(context).apply {
                    useController = false
                    player = exoPlayer

                    videoStatus = VideoStatus.PLAYING
                }
            }
        )
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = showUi,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = {
                            showUi = true
                        }
                    )
                    .background(blackColor)
                    .padding(horizontal = 12.dp)
                    .align(Alignment.BottomCenter)
            ) {
                ProgressBar(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.TopCenter),
                    height = 6.dp,
                    progress = playerProgress.toFloat() / totalDuration,
                    isStatic = false,
                    progressColor = Color.White,
                    trackColor = Color.Gray.copy(alpha = 0.3f),
                    onSoughtProgress = { progress ->
                        exoPlayer.playWhenReady = false
                        videoStatus = VideoStatus.PAUSED

                        playerProgress = (progress * totalDuration).roundToLong()
                        exoPlayer.seekTo(playerProgress)
                    },
                    onReleased = {
                        exoPlayer.playWhenReady = true
                        videoStatus = VideoStatus.PLAYING
                    }
                )
                IconButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .align(Alignment.Center),
                    icon = when (videoStatus) {
                        VideoStatus.PLAYING -> Icons.Default.Pause
                        VideoStatus.PAUSED -> Icons.Default.PlayArrow
                        VideoStatus.STOPPED -> Icons.Default.Replay
                    },
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    onClick = {
                        videoStatus = when (videoStatus) {
                            VideoStatus.PLAYING -> {
                                exoPlayer.playWhenReady = false
                                VideoStatus.PAUSED
                            }
                            VideoStatus.PAUSED -> {
                                exoPlayer.playWhenReady = true
                                VideoStatus.PLAYING
                            }
                            VideoStatus.STOPPED -> {
                                exoPlayer.seekTo(0L)
                                exoPlayer.playWhenReady = true
                                VideoStatus.PLAYING
                            }
                        }
                    },
                    isOutlined = false
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = "$leftTimeText / $totalTimeText",
                    style = LocalAppTheme.current.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}