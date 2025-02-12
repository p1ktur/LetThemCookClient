package com.letthemcook.editor.domain.editor.components.prototype

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.editor.domain.cooking.BlockCookingState
import com.letthemcook.editor.domain.cooking.CookingState
import com.letthemcook.editor.domain.cooking.track.EmptyTrackData
import com.letthemcook.editor.domain.cooking.track.ParallelTrackData
import com.letthemcook.editor.domain.cooking.track.SingleTrackData
import com.letthemcook.editor.domain.cooking.track.TrackData
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState
import com.letthemcook.editor.ui.drawing.COMPONENT_PADDING

// Graphics

fun Component.drawOn(
    context: Context,
    drawScope: DrawScope,
    textMeasurer: TextMeasurer,
    nameTextStyle: TextStyle,
    contentTextStyle: TextStyle,
    frameColor: Color,
    containerColor: Color,
    textColor: Color,
    highlightColor: Color,
    warningHighlightColor: Color,
    goodHighlightColor: Color,
    fileIcons: Map<FileType, VectorPainter>,
    positionXIsCentral: Boolean = false,
    canvasUiState: CanvasUiState
) {
    when (this) {
        is BlockComponent -> {
            drawOn(
                context = context,
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                warningHighlightColor = warningHighlightColor,
                goodHighlightColor = goodHighlightColor,
                positionXIsCentral = positionXIsCentral,
                canvasUiState = canvasUiState,
                fileIcons = fileIcons
            )
        }
        is HorizontalComposedComponent -> {
            drawOn(
                context = context,
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                warningHighlightColor = warningHighlightColor,
                goodHighlightColor = goodHighlightColor,
                positionXIsCentral = positionXIsCentral,
                canvasUiState = canvasUiState,
                fileIcons = fileIcons
            )
        }
        is VerticalComposedComponent -> {
            drawOn(
                context = context,
                drawScope = drawScope,
                textMeasurer = textMeasurer,
                nameTextStyle = nameTextStyle,
                contentTextStyle = contentTextStyle,
                frameColor = frameColor,
                containerColor = containerColor,
                textColor = textColor,
                highlightColor = highlightColor,
                warningHighlightColor = warningHighlightColor,
                goodHighlightColor = goodHighlightColor,
                positionXIsCentral = positionXIsCentral,
                canvasUiState = canvasUiState,
                fileIcons = fileIcons
            )
        }
    }
}

fun Component.isVisible(canvasUiState: CanvasUiState): Boolean {
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

suspend fun List<Component>.doForEveryChildAsync(action: suspend Component.() -> Unit) {
    forEach { it.action() }

    val childrenList = map { it.asComposed()?.components }

    childrenList.forEach { it?.doForEveryChildAsync(action) }
}

fun Component.countBlocks(): Int {
    var count = 0

    if (this is BlockComponent) return 1
    if (this is ComposedComponent) {
        components.doForEveryChild {
            (this as? BlockComponent)?.let { count++ }
        }
    }

    return count
}

fun Component.firstInHierarchy(): Component {
    return when (this) {
        is VerticalComposedComponent -> components.first().firstInHierarchy()
        else -> this
    }
}

fun Component.nextInHierarchy(): Component? {
    return when (val parent = parentComponent) {
        is HorizontalComposedComponent -> parent.nextInHierarchy()
        is VerticalComposedComponent -> {
            val thisIndex = parent.components.indexOf(this)

            if (thisIndex < parent.components.lastIndex) {
                parent.components[thisIndex + 1]
            } else {
                parent.nextInHierarchy()
            }
        }
        else -> null
    }
}

fun Component.previousInHierarchy(): Component? {
    return when (val parent = parentComponent) {
        is HorizontalComposedComponent -> parent.previousInHierarchy()
        is VerticalComposedComponent -> {
            val thisIndex = parent.components.indexOf(this)

            if (thisIndex > 0) {
                parent.components[thisIndex - 1]
            } else {
                parent.previousInHierarchy()
            }
        }
        else -> null
    }
}

// Cooking

fun Component.restore(globalCookingState: CookingState, recurse: Boolean = true) {
    when (this) {
        is BlockComponent -> {
            val previousInHierarchy = previousInHierarchy()
            val parent = parentComponent

            when {
                globalCookingState == CookingState.NOT_STARTED -> cookingState = BlockCookingState.NOT_REACHED
                cookingState == BlockCookingState.DONE -> cookingState = if (parent is HorizontalComposedComponent && recurse) {
                    BlockCookingState.COOKING
                } else {
                    BlockCookingState.NOT_REACHED
                }
                previousInHierarchy != null -> cookingState = BlockCookingState.NOT_REACHED
            }

            restoreTime()

            if (previousInHierarchy == null) return

            when (parent) {
                is HorizontalComposedComponent -> {
                    if (cookingState == BlockCookingState.NOT_REACHED && recurse) {
                        parent.restore(globalCookingState)
                    }

                    if (parent.components.all { it.isNotReached() }) {
                        if (globalCookingState != CookingState.NOT_STARTED) previousInHierarchy.cook()
                    }
                }
                is VerticalComposedComponent -> {
                    val thisIndex = parent.components.indexOf(this)

                    if (globalCookingState != CookingState.NOT_STARTED) {
                        if (thisIndex > 0) {
                            parent.components[thisIndex - 1].cook()
                        } else {
                            previousInHierarchy.cook()
                        }
                    }
                }
            }
        }
        is ComposedComponent -> {
            if (parentComponent is HorizontalComposedComponent && recurse) {
                parentComponent?.restore(globalCookingState)
            } else {
                components.forEach { it.restore(globalCookingState, false) }
            }
        }
    }
}

fun Component.cook() {
    if (canCook()) {
        when (this) {
            is BlockComponent -> cookingState = BlockCookingState.COOKING
            is HorizontalComposedComponent -> components.forEach { it.cook() }
            is VerticalComposedComponent -> if (components.all { it.isFinished() || it.isWaiting() }) {
                components.last().cook()
            } else {
                components.firstOrNull { !it.isCooking() }?.cook()
            }
        }
    }
}

fun Component.finish() {
    when (this) {
        is BlockComponent -> {
            cookingState = BlockCookingState.DONE

            val nextInHierarchy = nextInHierarchy() ?: return

            when (val parent = parentComponent) {
                is HorizontalComposedComponent -> {
                    if (parent.components.all { it.isWaiting() || it.isFinished() }) {
                        nextInHierarchy.cook()
                    }
                }
                is VerticalComposedComponent -> {
                    val thisIndex = parent.components.indexOf(this)

                    if (thisIndex < parent.components.lastIndex) {
                        parent.components[thisIndex + 1].cook()
                    } else {
                        nextInHierarchy.cook()
                    }
                }
            }
        }
        is ComposedComponent -> components.forEach { it.finish() }
    }
}

fun Component.isNotReached(): Boolean {
    return when (this) {
        is BlockComponent -> cookingState == BlockCookingState.NOT_REACHED
        is ComposedComponent -> components.all { it.isNotReached() }
        else -> false
    }
}

fun Component.isCooking(): Boolean {
    return when (this) {
        is BlockComponent -> cookingState == BlockCookingState.COOKING
        is ComposedComponent -> components.any { it.isCooking() }
        else -> false
    }
}

fun Component.isWaiting(): Boolean {
    return when (this) {
        is BlockComponent -> cookingState == BlockCookingState.WAITING
        is ComposedComponent -> components.all { it.isWaiting() }
        else -> false
    }
}

fun Component.isFinished(): Boolean {
    return when (this) {
        is BlockComponent -> getPretendedState() == BlockCookingState.DONE
        is ComposedComponent -> components.all { it.isFinished() }
        else -> false
    }
}

fun Component.canRestore(): Boolean {
    return previousInHierarchy() != null || parentComponent is HorizontalComposedComponent
}

fun Component.canCook(): Boolean {
    val previousInHierarchy = previousInHierarchy()
    return previousInHierarchy == null || previousInHierarchy.isFinished()
}

fun Component.canFinish(): Boolean {
    val parent = parentComponent

    return when (this) {
        is BlockComponent -> {
            if (parent is HorizontalComposedComponent) {
                if (parent.components.count { it.isWaiting() } < parent.components.size) {
                    true
                } else {
                    nextInHierarchy() != null && parent.canFinish()
                }
            } else {
                true
            }
        }
        is HorizontalComposedComponent -> {
            components.all { it.isWaiting() || it.isFinished() } && (parentComponent?.canFinish() ?: true)
        }
        is VerticalComposedComponent -> {
            components.any { !it.isFinished() } || nextInHierarchy() != null
        }
        else -> false
    }
}

// Other Cooking

fun List<Component>.decreaseCookingTimer() {
    doForEveryChild {
        if (this is BlockComponent && cookingState == BlockCookingState.COOKING && time > 0L) {
            time -= 1000L
        }
    }
}

fun Component.toTrackData(): TrackData {
    return when (this) {
        is BlockComponent -> SingleTrackData(this)
        is VerticalComposedComponent -> {
            val cookedChild = components.find { it.isCooking() || it.isWaiting() }

            cookedChild?.toTrackData() ?: components.last().toTrackData()
        }
        is HorizontalComposedComponent -> {
            ParallelTrackData(components.map { it.toTrackData() })
        }
        else -> EmptyTrackData
    }
}

fun Component.getTotalTime(): Long {
    return when (this) {
        is BlockComponent -> time
        is HorizontalComposedComponent -> components.maxOf { it.getTotalTime() }
        is VerticalComposedComponent -> components.sumOf { it.getTotalTime() }
        else -> 0L
    }
}

fun Component.countFinishedAndTotal(): Pair<Int, Int> {
    var cooked = 0
    var total = 0

    when (this) {
        is BlockComponent -> {
            if (isFinished()) cooked++
            total++
        }
        is ComposedComponent -> {
            val result = components.map {
                it.countFinishedAndTotal()
            }.reduce { acc, pair ->
                acc.plus(pair)
            }

            cooked += result.first
            total += result.second
        }
    }

    return cooked to total
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

fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return first + other.first to second + other.second
}