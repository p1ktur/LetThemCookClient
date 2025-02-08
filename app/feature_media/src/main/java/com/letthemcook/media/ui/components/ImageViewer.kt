package com.letthemcook.media.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset

@Composable
fun ImageViewer(
    modifier: Modifier = Modifier,
    bitmap: Bitmap
) {
    val imageScaleTarget = remember { mutableFloatStateOf(1f) }
    val imageScale by animateFloatAsState(
        targetValue = imageScaleTarget.floatValue
    )

    val imageOffsetTarget = remember { mutableStateOf(Offset.Zero) }
    val imageOffset by animateOffsetAsState(
        targetValue = imageOffsetTarget.value
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .scale(imageScale)
                .offset {
                    IntOffset(imageOffset.x.toInt(), imageOffset.y.toInt())
                }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        do {
                            val event: PointerEvent = awaitPointerEvent()

                            when (event.type) {
                                PointerEventType.Move -> {
                                    if (event.changes.size > 1) {
                                        imageScaleTarget.floatValue *= event.calculateZoom()
                                    } else {
                                        val pan = event.calculatePan()
                                        imageOffsetTarget.value += pan
                                    }

                                    event.changes.forEach {
                                        if (it.positionChange() != Offset.Zero) it.consume()
                                    }
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        imageScaleTarget.floatValue = 1f
                        imageOffsetTarget.value = Offset.Zero
                    }
                },
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Viewed Image",
            contentScale = ContentScale.FillWidth
        )
    }
}