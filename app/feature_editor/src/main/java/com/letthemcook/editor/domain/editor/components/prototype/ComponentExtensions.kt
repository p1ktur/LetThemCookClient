package com.letthemcook.editor.domain.editor.components.prototype

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiState
import com.letthemcook.editor.ui.drawing.COMPONENT_PADDING

// Graphics

fun Component.drawOn(
    drawScope: DrawScope,
    textMeasurer: TextMeasurer,
    nameTextStyle: TextStyle,
    contentTextStyle: TextStyle,
    frameColor: Color,
    containerColor: Color,
    textColor: Color,
    highlightColor: Color,
    positionXIsCentral: Boolean = false,
    canvasUiState: BuilderUiState.CanvasUiState
) {
    when (this) {
        is BlockComponent -> {
            drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                positionXIsCentral = positionXIsCentral,
                canvasUiState = canvasUiState
            )
        }
        is HorizontalComposedComponent -> {
            drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                positionXIsCentral = positionXIsCentral,
                canvasUiState = canvasUiState
            )
        }
        is VerticalComposedComponent -> {
            drawOn(
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                positionXIsCentral = positionXIsCentral,
                canvasUiState = canvasUiState
            )
        }
    }
}

fun Component.isVisible(canvasUiState: BuilderUiState.CanvasUiState): Boolean {
    val canvasTopLeft = Offset.Zero
    val canvasBottomRight = Offset(canvasUiState.size.width, canvasUiState.size.height)

    val canvasLeft = canvasTopLeft.x
    val canvasRight = canvasBottomRight.x
    val canvasTop = canvasTopLeft.y
    val canvasBottom = canvasBottomRight.y

    val topLeft = canvasUiState.undoScaleAndTranslate(position)
    val topRight = canvasUiState.undoScaleAndTranslate(position + Offset(size.width, 0f))
    val bottomLeft = canvasUiState.undoScaleAndTranslate(position + Offset(0f, size.height))
    val bottomRight = canvasUiState.undoScaleAndTranslate(position + Offset(size.width, size.height))

    var verticesCounter = 0

    if (topLeft.x in canvasLeft..canvasRight && topLeft.y in canvasTop..canvasBottom) verticesCounter++
    if (topRight.x in canvasLeft..canvasRight && topRight.y in canvasTop..canvasBottom) verticesCounter++
    if (bottomLeft.x in canvasLeft..canvasRight && bottomLeft.y in canvasTop..canvasBottom) verticesCounter++
    if (bottomRight.x in canvasLeft..canvasRight && bottomRight.y in canvasTop..canvasBottom) verticesCounter++

    return verticesCounter > 0
}

// Components

fun Component.insertTopComponent(insertedComponent: Component): Component? {
    when (this) {
        is BlockComponent, is HorizontalComposedComponent -> {
            val parentAsVertical = parentComponent as? VerticalComposedComponent

            if (parentAsVertical != null) {
                val indexToInsert = parentAsVertical.components.indexOf(this)
                parentAsVertical.components.add(indexToInsert, insertedComponent)

                insertedComponent.parentComponent = parentAsVertical
            } else {
                val composedComponent = VerticalComposedComponent(
                    components = mutableListOf(insertedComponent, this)
                )

                replaceInParent(composedComponent)

                this.parentComponent = composedComponent
                insertedComponent.parentComponent = composedComponent

                return composedComponent
            }
        }
        is VerticalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(0, insertedComponent)
        }
    }

    return null
}

fun Component.insertBottomComponent(insertedComponent: Component): Component? {
    when (this) {
        is BlockComponent, is HorizontalComposedComponent -> {
            val parentAsVertical = parentComponent as? VerticalComposedComponent

            if (parentAsVertical != null) {
                val indexToInsert = parentAsVertical.components.indexOf(this) + 1
                parentAsVertical.components.add(indexToInsert, insertedComponent)

                insertedComponent.parentComponent = parentAsVertical
            } else {
                val composedComponent = VerticalComposedComponent(
                    components = mutableListOf(this, insertedComponent)
                )

                replaceInParent(composedComponent)

                this.parentComponent = composedComponent
                insertedComponent.parentComponent = composedComponent

                return composedComponent
            }
        }
        is VerticalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(insertedComponent)
        }
    }

    return null
}

fun Component.insertLeftComponent(insertedComponent: Component): Component? {
    when (this) {
        is BlockComponent, is VerticalComposedComponent -> {
            val composedComponent = HorizontalComposedComponent(
                components = mutableListOf(insertedComponent, this)
            )

            replaceInParent(composedComponent)

            this.parentComponent = composedComponent
            insertedComponent.parentComponent = composedComponent

            return composedComponent
        }
        is HorizontalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(0, insertedComponent)
        }
    }

    return null
}

fun Component.insertRightComponent(insertedComponent: Component): Component? {
    when (this) {
        is BlockComponent, is VerticalComposedComponent -> {
            val composedComponent = HorizontalComposedComponent(
                components = mutableListOf(this, insertedComponent)
            )

            replaceInParent(composedComponent)

            this.parentComponent = composedComponent
            insertedComponent.parentComponent = composedComponent

            return composedComponent
        }
        is HorizontalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(insertedComponent)
        }
    }

    return null
}

fun Component.replaceInParent(otherComponent: Component) {
    parentComponent?.asComposed()?.let { parent ->
        this.parentComponent = null
        otherComponent.parentComponent = parent

        val thisIndex = parent.components.indexOf(this)
        parent.components[thisIndex] = otherComponent
    }
}

fun Component.removeFromHierarchy(setCentralComponent: (Component) -> Unit) {
    parentComponent?.asComposed()?.let { parent ->
        val mustReplace = parent.components.size < 3

        if (mustReplace) {
            val parentsParent = parent.parentComponent?.asComposed()
            val otherComponent = parent.components.find { it != this } ?: return

            if (parentsParent != null) {
                parent.replaceInParent(otherComponent)
            } else {
                otherComponent.parentComponent = null
                setCentralComponent(otherComponent)
            }
        } else {
            this.parentComponent = null
            parent.components.remove(this)
        }
    }
}

fun List<Component>.findInHierarchy(target: Component): Component? {
    if (this.size == 1 && first() == target) return first()

    val childrenList = map { it.asComposed()?.components }

    childrenList.forEach { children ->
        val potentialTarget = children?.find { it == target }

        if (potentialTarget != null) return potentialTarget

        children?.findInHierarchy(target)?.let { component ->
            return component
        }
    }

    return null
}

fun List<Component>.doForEveryChild(action: Component.() -> Unit) {
    forEach { it.action() }

    val childrenList = map { it.asComposed()?.components }

    childrenList.forEach { it?.doForEveryChild(action) }
}

// Geometry

fun Component.pointInBounds(point: Offset): Boolean {
    val horizontalPadding = if (this is ComposedComponent) COMPONENT_PADDING else 0f
    val verticalPadding = if (this is ComposedComponent) 0f else -COMPONENT_PADDING

    val contains = point.x in (position.x - horizontalPadding..position.x + size.width + horizontalPadding) &&
            point.y in (position.y - verticalPadding..position.y + size.height + verticalPadding)

    return contains
}

fun Component.definePointRelation(pointPosition: Offset): Relation {
    val topLeft = position
    val bottomRight = position + Offset(size.width, size.height)
    val slope = (bottomRight.y - topLeft.y) / (bottomRight.x - topLeft.x)
    val b = topLeft.y - slope * topLeft.x

    val x0 = pointPosition.x
    val y0 = pointPosition.y
    val x1 = (y0 - b) / slope
    val x2 = ((topLeft.y * 2 + size.height - y0) - b) / slope

    return when {
        x0 < x1 && x0 < x2 -> Relation.Left
        x0 > x1 && x0 < x2 -> Relation.Top
        x0 > x1 && x0 > x2 -> Relation.Right
        else -> Relation.Bottom

    }
}

fun List<Component>.getPointerContainer(
    point: Offset,
    onlyBlocks: Boolean = false,
    strict: Boolean = false
): Component? {
    if (this.size == 1 && first() is BlockComponent) {
        if (strict) {
            if (first().pointInBounds(point)) return first()
        } else {
            return first()
        }
    }

    var currentComponent = find { it.pointInBounds(point) }
    var foundComponent = false

    if (onlyBlocks) {
        while (currentComponent !is BlockComponent) {
            currentComponent = (currentComponent as? ComposedComponent)?.components?.find { it.pointInBounds(point) }
            if (currentComponent == null) break
        }
    } else {
        while (!foundComponent && currentComponent != null) {
            val lastComponent = currentComponent
            currentComponent = (currentComponent as? ComposedComponent)?.components?.find { it.pointInBounds(point) }

            if (currentComponent == null) {
                currentComponent = lastComponent
                foundComponent = true
            }

            if (currentComponent is BlockComponent) {
                foundComponent = true
            }
        }
    }

    return currentComponent
}

// Other

fun List<Component>.componentHashCodes(): Int {
    if (isEmpty()) return hashCode()
    var currentHashCode: Int = first().hashCode()

    for (i in 1 until size) {
        currentHashCode = currentHashCode xor get(i).hashCode()
    }

    return currentHashCode
}

fun Component.asComposed(): ComposedComponent? {
    return this as? ComposedComponent
}