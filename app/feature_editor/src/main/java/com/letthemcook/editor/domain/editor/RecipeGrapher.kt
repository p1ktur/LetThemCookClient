package com.letthemcook.editor.domain.editor

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.prototype.drawOn
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiState
import com.letthemcook.editor.ui.drawing.DRAW_PADDING

object RecipeGrapher {
    fun drawRecipeGraph(
        // Data
        startComponent: StartComponent,
        centralComponent: Component,
        endComponent: EndComponent,
        canvasUiState: BuilderUiState.CanvasUiState,
        // Graphics
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        titleTextStyle: TextStyle,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle,
        frameColor: Color,
        containerColor: Color,
        textColor: Color,
        highlightColor: Color
    ) {
        if (centralComponent is EmptyComponent) {
            val circleTextLayout = textMeasurer.measure(
                text = "Drag Block Here",
                style = titleTextStyle
            )

            val circleCenter = (startComponent.position + endComponent.position) / 2f +
                    Offset(startComponent.size.width / 4, startComponent.size.height / 4) +
                    Offset(endComponent.size.width / 4, endComponent.size.height / 4)
            val circleRadius = circleTextLayout.size.width / 2 + DRAW_PADDING * 4

            startComponent.position = Offset(drawScope.size.width / 2 , drawScope.size.height * 0.1f) +
                    startComponent.calculateSize(textMeasurer, titleTextStyle).run { Offset(-width / 2f, -height / 2f) }
            endComponent.position = Offset(drawScope.size.width / 2 , drawScope.size.height * 0.9f) +
                    endComponent.calculateSize(textMeasurer, titleTextStyle).run { Offset(-width / 2f, -height / 2f) }

            startComponent.drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                textColor = textColor,
                containerColor = containerColor,
                frameColor = frameColor,
                textStyle = titleTextStyle,
                canvasUiState = canvasUiState
            )
            drawScope.drawLine(
                color = frameColor,
                start = startComponent.position + Offset(startComponent.size.width / 2, startComponent.size.height),
                end = circleCenter.copy(circleCenter.x, circleCenter.y - circleRadius),
                strokeWidth = 4f
            )
            drawScope.drawCircle(
                color = containerColor.copy(alpha = 0.5f),
                center = circleCenter,
                radius = circleRadius,
                style = Fill
            )
            drawScope.drawCircle(
                color = frameColor.copy(alpha = 0.5f),
                center = circleCenter,
                radius = circleRadius,
                style = Stroke(4f)
            )
            drawScope.drawLine(
                color = frameColor,
                start = circleCenter.copy(circleCenter.x, circleCenter.y + circleRadius),
                end = endComponent.position + Offset(endComponent.size.width / 2, 0f),
                strokeWidth = 4f
            )
            drawScope.drawText(
                textLayoutResult = circleTextLayout,
                color = textColor,
                topLeft = circleCenter - Offset(circleTextLayout.size.width / 2f, circleTextLayout.size.height / 2f)
            )
            endComponent.drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                textColor = textColor,
                containerColor = containerColor,
                frameColor = frameColor,
                textStyle = titleTextStyle,
                canvasUiState = canvasUiState
            )
        } else {
            startComponent.drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                textColor = textColor,
                containerColor = containerColor,
                frameColor = frameColor,
                textStyle = titleTextStyle,
                canvasUiState = canvasUiState
            )

            var cursorPosition: Offset = startComponent.position + Offset(startComponent.size.width / 2, startComponent.size.height)

            centralComponent.position = cursorPosition
            centralComponent.drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                positionXIsCentral = true,
                canvasUiState = canvasUiState
            )

            cursorPosition += Offset(0f, centralComponent.size.height)
            endComponent.position = cursorPosition
            endComponent.drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                textColor = textColor,
                containerColor = containerColor,
                frameColor = frameColor,
                textStyle = titleTextStyle,
                positionXIsCentral = true,
                canvasUiState = canvasUiState
            )
        }
    }
}