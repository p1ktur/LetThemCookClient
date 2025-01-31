package com.letthemcook.editor.ui.components.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.editor.domain.editor.RecipeGrapher
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiAction
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiState
import com.letthemcook.editor.ui.drawing.drawCenterHelper
import com.letthemcook.editor.ui.drawing.drawGrid
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun CookingCanvas(
    modifier: Modifier = Modifier,
    uiState: CookingUiState,
    textMeasurer: TextMeasurer,
    blockComponentTitleTextStyle: TextStyle,
    blockComponentNameTextStyle: TextStyle,
    blockComponentContentTextStyle: TextStyle,
    onUiAction: (CookingUiAction) -> Any?
) {
    // Canvas
    val highlightColor = LocalAppTheme.current.highlightColor
    val warningHighlightColor = LocalAppTheme.current.warningHighlightColor
    val goodHighlightColor = LocalAppTheme.current.goodHighlightColor
    val backgroundColor = LocalAppTheme.current.canvasBackground
    val gridColor = LocalAppTheme.current.canvasGrid
    // Component
    val frameColor = LocalAppTheme.current.text
    val containerColor = LocalAppTheme.current.container
    val componentTextColor = LocalAppTheme.current.text
    val fileIcons = mapOf(
        FileType.IMAGE to rememberVectorPainter(Icons.Default.Image),
        FileType.VIDEO to rememberVectorPainter(Icons.Default.VideoFile),
        FileType.ANY to rememberVectorPainter(Icons.Default.FilePresent)
    )

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event: PointerEvent = awaitPointerEvent()
                        val position = event.changes.first().position

                        when (event.type) {
                            PointerEventType.Press -> onUiAction(
                                CookingUiAction.PointerDown(
                                    position
                                )
                            )

                            PointerEventType.Move -> {
                                if (event.changes.size > 1) {
                                    onUiAction(CookingUiAction.PointerZoom(event.calculateZoom()))
                                } else {
                                    val pan = event.calculatePan()
                                    if (pan != Offset.Zero) {
                                        onUiAction(
                                            CookingUiAction.PointerMove(
                                                event.changes.first().position,
                                                pan
                                            )
                                        )
                                    }
                                }

                                event.changes.forEach {
                                    if (it.positionChange() != Offset.Zero) it.consume()
                                }
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    onUiAction(CookingUiAction.PointerRelease)
                }
            }
            .onSizeChanged { size ->
                onUiAction(
                    CookingUiAction.UpdateCanvasSize(
                        Size(
                            size.width.toFloat(),
                            size.height.toFloat()
                        )
                    )
                )
            }
    ) {
        uiState.canvasCounter.let {
            drawGrid(
                step = 48 * (1.5f + (uiState.canvasUiState.zoom) % 0.5f),
                canvasZoom = uiState.canvasUiState.zoom,
                canvasOffset = uiState.canvasUiState.offset,
                backgroundColor = backgroundColor,
                gridColor = gridColor.copy(alpha = 0.6f)
            )
            scale(uiState.canvasUiState.zoom) {
                translate(uiState.canvasUiState.offset.x, uiState.canvasUiState.offset.y) {
                    drawCenterHelper(
                        squareSize = 24f,
                        gridColor = gridColor,
                        strokeWidth = 1.5f / uiState.canvasUiState.zoom
                    )

                    RecipeGrapher.drawRecipeGraph(
                        // Data
                        startComponent = uiState.startComponent,
                        centralComponent = uiState.centralComponent,
                        endComponent = uiState.endComponent,
                        canvasUiState = uiState.canvasUiState,
                        // Graphics
                        drawScope = this,
                        textMeasurer = textMeasurer,
                        titleTextStyle = blockComponentTitleTextStyle,
                        nameTextStyle = blockComponentNameTextStyle,
                        contentTextStyle = blockComponentContentTextStyle,
                        frameColor = frameColor,
                        containerColor = containerColor,
                        textColor = componentTextColor,
                        highlightColor = highlightColor,
                        warningHighlightColor = warningHighlightColor,
                        goodHighlightColor = goodHighlightColor,
                        fileIcons = fileIcons
                    )
                }
            }
        }
    }
}