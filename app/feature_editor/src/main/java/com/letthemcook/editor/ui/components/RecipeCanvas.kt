package com.letthemcook.editor.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.editor.domain.editor.RecipeGrapher
import com.letthemcook.editor.domain.editor.canvasButtons.DeleteIcon.Companion.rememberDeleteIcon
import com.letthemcook.editor.domain.editor.components.prototype.ComponentFocus
import com.letthemcook.editor.domain.editor.geometry.zoom
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiState
import com.letthemcook.editor.ui.components.CanvasButtonsPointerManager.isPointerOnDeleteIcon
import com.letthemcook.editor.ui.drawing.DRAW_PADDING
import com.letthemcook.editor.ui.drawing.drawCenterHelper
import com.letthemcook.editor.ui.drawing.drawGrid
import com.letthemcook.editor.ui.drawing.drawProductLabel
import com.letthemcook.theme.base.LocalAppTheme

object CanvasButtonsPointerManager {
    var isPointerOnDeleteIcon = false
}

// TODO check if blocks are visible and the draw them

@Composable
fun RecipeCanvas(
    modifier: Modifier = Modifier,
    uiState: BuilderUiState,
    textMeasurer: TextMeasurer,
    blockComponentTitleTextStyle: TextStyle,
    blockComponentNameTextStyle: TextStyle,
    blockComponentContentTextStyle: TextStyle,
    onUiAction: (BuilderUiAction) -> Any?
) {
    // Canvas
    val highlightColor = LocalAppTheme.current.highlightColor
    val backgroundColor = LocalAppTheme.current.canvasBackground
    val gridColor = LocalAppTheme.current.canvasGrid
    val buttonsContentColor = LocalAppTheme.current.text
    // Component
    val frameColor = LocalAppTheme.current.text
    val containerColor = LocalAppTheme.current.container
    val componentTextColor = LocalAppTheme.current.text
    // Delete Icon
    val deleteIcon = rememberDeleteIcon(uiState)
    val deleteIconIsVisible = remember(uiState.componentFocus) { uiState.componentFocus is ComponentFocus.Block }
    val pointerIsOverDeleteIcon by remember(uiState.canvasUiState.pointerMoveOffset, uiState.componentFocus) {
        derivedStateOf {
            uiState.componentFocus is ComponentFocus.Block &&
                    uiState.canvasUiState.pointerMoveOffset != null &&
                    uiState.canvasUiState.pointerMoveOffset.run {
                        x in deleteIcon.position.x..deleteIcon.position.x + deleteIcon.size.width &&
                                y in deleteIcon.position.y..deleteIcon.position.y + deleteIcon.size.height
                    }
        }.apply {
            isPointerOnDeleteIcon = value
        }
    }
    val deleteIconContainerColor by animateColorAsState(
        targetValue = if (pointerIsOverDeleteIcon) LocalAppTheme.current.badHighlightColor else LocalAppTheme.current.screenOne,
        label = "Delete Icon Highlighting"
    )
    val deleteIconAlpha by animateFloatAsState(
        targetValue = if (deleteIconIsVisible) 1f else 0f,
        label = "Delete Icon Appearance"
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
                                BuilderUiAction.PointerDown(
                                    position
                                )
                            )

                            PointerEventType.Move -> {
                                if (event.changes.size > 1) {
                                    onUiAction(BuilderUiAction.PointerZoom(event.calculateZoom()))
                                } else {
                                    val pan = event.calculatePan()
                                    if (pan != Offset.Zero) {
                                        onUiAction(
                                            BuilderUiAction.PointerMove(
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

                    if (isPointerOnDeleteIcon) onUiAction(BuilderUiAction.RemoveComponent)
                    onUiAction(BuilderUiAction.PointerRelease)
                }
            }
            .onSizeChanged { size ->
                onUiAction(
                    BuilderUiAction.UpdateCanvasSize(
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
                step = 48f * uiState.canvasUiState.zoom,
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
                        highlightColor = highlightColor
                    )

                    when (uiState.componentFocus) {
                        ComponentFocus.None -> Unit
                        is ComponentFocus.Block -> run {
                            uiState.componentFocus.ref.drawDraggableOn(
                                drawScope = this,
                                textMeasurer = textMeasurer,
                                nameTextStyle = blockComponentNameTextStyle,
                                contentTextStyle = blockComponentContentTextStyle,
                                frameColor = frameColor,
                                containerColor = containerColor,
                                textColor = componentTextColor,
                                centerPosition = uiState.canvasUiState.scaleAndTranslate(
                                    position = uiState.canvasUiState.pointerMoveOffset ?:
                                    uiState.canvasUiState.pointerDownOffset ?:
                                    return@run
                                )
                            )
                        }
                        is ComponentFocus.Product -> {
                            var labelDrawOffset = uiState.canvasUiState.pointerMoveOffset ?: uiState.canvasUiState.pointerDownOffset ?: Offset.Zero
                            labelDrawOffset = labelDrawOffset.zoom(uiState.canvasUiState.center, uiState.canvasUiState.zoom) - uiState.canvasUiState.offset

                            val productNameLayout = textMeasurer.measure(
                                text = uiState.componentFocus.data.name,
                                style = blockComponentContentTextStyle
                            )

                            drawProductLabel(
                                textLayout = productNameLayout,
                                topLeft = labelDrawOffset.minus(
                                    Offset(productNameLayout.size.width / 2f, productNameLayout.size.height / 2f)
                                ),
                                padding = DRAW_PADDING,
                                color = componentTextColor,
                                textColor = containerColor
                            )
                        }
                    }
                }
            }
        }

        deleteIcon.drawOn(
            drawScope = this,
            contentColor = buttonsContentColor,
            containerColor = deleteIconContainerColor,
            alpha = deleteIconAlpha
        )
    }
}