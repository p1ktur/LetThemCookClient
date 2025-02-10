package com.letthemcook.theme.components.bars

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    height: Dp,
    progress: Float,
    isStatic: Boolean = true,
    progressColor: Color = LocalAppTheme.current.text,
    trackColor: Color = LocalAppTheme.current.screenOne,
    onSoughtProgress: ((Float) -> Unit)? = null,
    onReleased: (() -> Unit)? = null
) {
    val density = LocalDensity.current
    val twoDpsInPixels = with (density) { 2.dp.toPx() }

    var soughtProgress: Float? by remember { mutableStateOf(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .run {
                if (onSoughtProgress != null) {
                    pointerInput(Unit) {
                        awaitEachGesture {
                            var event = awaitFirstDown()
                            var position = event.position
                            var viewedProgress = position.x / size.width

                            do {
                                soughtProgress = viewedProgress
                                onSoughtProgress.invoke(viewedProgress)

                                event.consume()

                                event = awaitPointerEvent().changes.firstOrNull() ?: continue

                                position = event.position
                                viewedProgress = position.x / size.width
                            } while (event.pressed)

                            onReleased?.invoke()
                            soughtProgress = null
                        }
                    }
                } else this
            }
            .drawBehind {
                val progressWidth = size.width * progress

                if (isStatic) {
                    drawRect(
                        color = trackColor
                    )
                    drawRect(
                        color = progressColor,
                        size = size.copy(width = progressWidth)
                    )
                } else {
                    drawRoundRect(
                        color = trackColor,
                        cornerRadius = CornerRadius(twoDpsInPixels, twoDpsInPixels)
                    )
                    drawRoundRect(
                        color = progressColor,
                        size = size.copy(width = progressWidth),
                        cornerRadius = CornerRadius(twoDpsInPixels, twoDpsInPixels)
                    )

                    soughtProgress?.let {
                        drawCircle(
                            color = progressColor,
                            center = center.copy(x = progressWidth),
                            radius = twoDpsInPixels * 4
                        )
                    }
                }
            }
    )
}