package com.letthemcook.editor.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.editor.domain.editor.RecipeGrapher
import com.letthemcook.editor.domain.editor.geometry.zoom
import com.letthemcook.editor.domain.editor.deleteIcon.DeleteIcon.Companion.rememberDeleteIcon
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiState
import com.letthemcook.editor.ui.components.DeleteIconPointerDataWrapper.isPointerOn
import com.letthemcook.editor.ui.drawing.DRAW_PADDING
import com.letthemcook.editor.ui.drawing.drawCenterHelper
import com.letthemcook.editor.ui.drawing.drawGrid
import com.letthemcook.editor.ui.drawing.drawProductLabel
import com.letthemcook.theme.base.LocalAppTheme

object DeleteIconPointerDataWrapper {
    var isPointerOn = false
}

@Composable
fun RecipeCanvas(
    modifier: Modifier = Modifier,
    uiState: BuilderUiState,
    textMeasurer: TextMeasurer,
    blockComponentTitleTextStyle: TextStyle,
    blockComponentNameTextStyle: TextStyle,
    blockComponentContentTextStyle: TextStyle,
    onUiAction: (BuilderUiAction) -> Unit
) {
    // Canvas
    val highlightColor = LocalAppTheme.current.highlightColor
    val backgroundColor = LocalAppTheme.current.canvasBackground
    val gridColor = LocalAppTheme.current.canvasGrid
    // Component
    val frameColor = LocalAppTheme.current.text
    val containerColor = LocalAppTheme.current.container
    val componentTextColor = LocalAppTheme.current.text
    // Delete Icon
    val deleteIcon = rememberDeleteIcon(uiState)
    val deleteIconIsVisible = remember(uiState.componentFocus) { uiState.componentFocus == BuilderUiState.ComponentFocus.BLOCK }
    val pointerIsOverDeleteIcon by remember(uiState.canvasUiState.pointerMoveOffset, uiState.componentFocus) {
        derivedStateOf {
            uiState.componentFocus == BuilderUiState.ComponentFocus.BLOCK &&
                    uiState.canvasUiState.pointerMoveOffset != null &&
                    uiState.canvasUiState.pointerMoveOffset.run {
                        x in deleteIcon.position.x..deleteIcon.position.x + deleteIcon.size.width &&
                                y in deleteIcon.position.y..deleteIcon.position.y + deleteIcon.size.height
                    }
        }.apply {
            isPointerOn = value
        }
    }
    val deleteIconContentColor = LocalAppTheme.current.text
    val deleteIconContainerColor by animateColorAsState(
        targetValue = if (pointerIsOverDeleteIcon) LocalAppTheme.current.highlightColor else LocalAppTheme.current.screenOne,
        label = "Delete Icon Highlighting"
    )
    val deleteIconAlpha by animateFloatAsState(
        targetValue = if (deleteIconIsVisible) 1f else 0f,
        label = "Delete Icon Appearance"
    )

    // Grapher
    val recipeGrapher = remember { RecipeGrapher() }

    Canvas(
        modifier = modifier
            .pointerInput(uiState.blockComponents) {
                awaitEachGesture {
                    val pointerInputChange = awaitFirstDown()
                    onUiAction(BuilderUiAction.PointerDown(pointerInputChange.position))

                    do {
                        val event: PointerEvent = awaitPointerEvent()

                        if (event.changes.size > 1) {
                            onUiAction(BuilderUiAction.PointerZoom(event.calculateZoom()))
                        } else {
                            val pan = event.calculatePan()
                            if (pan != Offset.Zero) {
                                onUiAction(
                                    BuilderUiAction.PointerMove(
                                        event.changes.first().position,
                                        event.calculatePan()
                                    )
                                )
                            }
                        }

                        event.changes.forEach {
                            if (it.positionChange() != Offset.Zero) it.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    if (isPointerOn) {
                        onUiAction(BuilderUiAction.RemoveComponent(uiState.blockComponents.lastIndex))
                    }
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

                    recipeGrapher.drawRecipeGraph(
                        startComponent = uiState.startComponent,
                        endComponent = uiState.endComponent,
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

                    if (uiState.movedProducts.isNotEmpty()) {
                        var labelDrawOffset = uiState.canvasUiState.pointerMoveOffset ?: uiState.canvasUiState.pointerDownOffset ?: Offset.Zero
                        labelDrawOffset = labelDrawOffset.zoom(uiState.canvasUiState.center, uiState.canvasUiState.zoom) - uiState.canvasUiState.offset

                        uiState.movedProducts.forEach {
                            val productNameLayout = textMeasurer.measure(
                                text = it.name,
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
            contentColor = deleteIconContentColor,
            containerColor = deleteIconContainerColor,
            alpha = deleteIconAlpha
        )
    }
}