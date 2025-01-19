package com.letthemcook.editor.domain.editor

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.letthemcook.editor.domain.editor.components.BlockComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.ui.drawing.DRAW_PADDING

class RecipeGrapher {

    fun drawRecipeGraph(
        // Data
        startComponent: StartComponent,
        endComponent: EndComponent,
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
        if (startComponent.nextComponent is EndComponent) {
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
                textStyle = titleTextStyle
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
                textStyle = titleTextStyle
            )
        } else {
            getAndDrawGraph(
                startComponent = startComponent,
                onDrawComponent = { position, component ->
                    component.position = position

                    when (component) {
                        is BlockComponent -> {
                            component.drawOn(
                                drawScope = drawScope,
                                textMeasurer = textMeasurer,
                                nameTextStyle = nameTextStyle,
                                contentTextStyle = contentTextStyle,
                                frameColor = frameColor,
                                containerColor = containerColor,
                                textColor = textColor,
                                highlightColor = highlightColor,
                                positionXIsCentral = true
                            )
                        }
                        is HorizontalComposedComponent -> {
                            component.drawOn(
                                drawScope = drawScope,
                                textMeasurer = textMeasurer,
                                nameTextStyle = nameTextStyle,
                                contentTextStyle = contentTextStyle,
                                frameColor = frameColor,
                                containerColor = containerColor,
                                textColor = textColor,
                                highlightColor = highlightColor,
                                positionXIsCentral = true
                            )
                        }
                        is VerticalComposedComponent -> {
                            component.drawOn(
                                drawScope = drawScope,
                                textMeasurer = textMeasurer,
                                nameTextStyle = nameTextStyle,
                                contentTextStyle = contentTextStyle,
                                frameColor = frameColor,
                                containerColor = containerColor,
                                textColor = textColor,
                                highlightColor = highlightColor,
                                positionXIsCentral = true
                            )
                        }
                        is StartComponent -> {
                            component.drawOn(
                                drawScope = drawScope,
                                textMeasurer = textMeasurer,
                                textColor = textColor,
                                containerColor = containerColor,
                                frameColor = frameColor,
                                textStyle = titleTextStyle
                            )
                        }
                        is EndComponent -> {
                            component.drawOn(
                                drawScope = drawScope,
                                textMeasurer = textMeasurer,
                                textColor = textColor,
                                containerColor = containerColor,
                                frameColor = frameColor,
                                textStyle = titleTextStyle,
                                positionXIsCentral = true
                            )
                        }
                    }
                }
            )
        }
    }

    private inline fun getAndDrawGraph(
        startComponent: StartComponent,
        onDrawComponent: (Offset, Component) -> Unit
    ) {
//        Log.d("TAG", "NEW SESSION ${startComponent.getChainUntilEnd()}")
        onDrawComponent(startComponent.position, startComponent)

        var currentComponent = startComponent.nextComponent
        var cursorPosition: Offset = startComponent.position + Offset(startComponent.size.width / 2, startComponent.size.height)

        while (currentComponent !is EndComponent && currentComponent !is EmptyComponent) {
            onDrawComponent(cursorPosition, currentComponent)
            cursorPosition += Offset(0f, currentComponent.size.height)
            currentComponent = currentComponent.nextComponent
        }

        onDrawComponent(cursorPosition, currentComponent)
    }
}