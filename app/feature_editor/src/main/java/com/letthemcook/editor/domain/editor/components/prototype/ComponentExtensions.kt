package com.letthemcook.editor.domain.editor.components.prototype

import android.util.Log
import android.util.Log.e
import androidx.compose.ui.geometry.Offset
import com.letthemcook.editor.domain.editor.components.BlockComponent
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EmptyComponent.nextComponent
import com.letthemcook.editor.domain.editor.components.EmptyComponent.prevComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.domain.editor.components.containment.ComponentContainment
import com.letthemcook.editor.domain.editor.components.containment.Relation
import com.letthemcook.editor.domain.editor.geometry.smoothen
import com.letthemcook.editor.ui.drawing.HORIZONTAL_COMPONENT_PADDING
import com.letthemcook.editor.ui.drawing.MIN_LINE_LENGTH

// Graphics

// TODO Highlight all components or whole component with background when shading it or their parent
// TODO make dragging normal
// TODO make removal normal
// TODO Clean code

fun Component.moveTo(position: Offset) {
    this.position = (position - (containedPointerPosition ?: Offset.Zero)).smoothen()
}

fun Component.containsPointerWhole(pointerOffset: Offset, isDown: Boolean): Boolean {
    if (isDown) containedPointerPosition = pointerOffset - position

    return pointerOffset.x in position.x..position.x + size.width &&
            pointerOffset.y in position.y..position.y + size.height
}

// Components

fun Component.insertTopComponent(insertedComponent: Component) {
    when (this) {
        is BlockComponent, is HorizontalComposedComponent -> {
            val parentAsVertical = parentComponent as? VerticalComposedComponent

            if (parentAsVertical != null) {
                val indexToInsert = parentAsVertical.components.indexOf(this)
                parentAsVertical.components.add(indexToInsert, insertedComponent)

                if (indexToInsert == 0) {
                    this.parentComponent = null
                    insertedComponent.parentComponent = parentAsVertical
                }
            } else {
                val composedComponent = VerticalComposedComponent(
                    components = mutableListOf(insertedComponent, this),
                    prevComponent = this.prevComponent,
                    nextComponent = this.nextComponent
                )

                replaceItself(composedComponent)

                this.prevComponent = EmptyComponent
                this.nextComponent = EmptyComponent

                this.parentComponent = composedComponent
                insertedComponent.parentComponent = composedComponent
            }
        }
        is VerticalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(0, insertedComponent)
        }
//        is EndComponent -> {
//            val convertedParent = parentComponent?.asComposed()
//            val hasParent = convertedParent != null
//
//            insertedComponent.nextComponent = this
//            insertedComponent.prevComponent = prevComponent
//            insertedComponent.parentComponent = parentComponent
//
//            parentComponent = null
//            prevComponent.nextComponent = insertedComponent
//            this.prevComponent = insertedComponent
//
//            if (hasParent) {
//                convertedParent?.let { parent ->
//                    val thisIndex = parent.components.indexOf(this)
//                    parent.components[thisIndex] = insertedComponent
//                }
//            }
//        }
    }
}

fun Component.insertBottomComponent(insertedComponent: Component) {
    when (this) {
        is BlockComponent, is HorizontalComposedComponent -> {
            val parentAsVertical = parentComponent as? VerticalComposedComponent

            if (parentAsVertical != null) {
                val indexToInsert = parentAsVertical.components.indexOf(this) + 1
                parentAsVertical.components.add(indexToInsert, insertedComponent)
            } else {
                val composedComponent = VerticalComposedComponent(
                    components = mutableListOf(this, insertedComponent),
                    prevComponent = this.prevComponent,
                    nextComponent = this.nextComponent
                )

                replaceItself(composedComponent)

                this.prevComponent = EmptyComponent
                this.nextComponent = EmptyComponent

                this.parentComponent = composedComponent
                insertedComponent.parentComponent = composedComponent
            }
        }
        is VerticalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(insertedComponent)
        }
        is StartComponent -> {
            insertedComponent.nextComponent = nextComponent
            insertedComponent.prevComponent = this

            nextComponent.prevComponent = insertedComponent
            this.nextComponent = insertedComponent
        }
    }
}

fun Component.insertLeftComponent(insertedComponent: Component) {
    when (this) {
        is BlockComponent, is VerticalComposedComponent -> {
            val composedComponent = HorizontalComposedComponent(
                components = mutableListOf(insertedComponent, this),
                prevComponent = this.prevComponent,
                nextComponent = this.nextComponent
            )

            replaceItself(composedComponent)

            this.prevComponent = EmptyComponent
            this.nextComponent = EmptyComponent

            this.parentComponent = composedComponent
            insertedComponent.parentComponent = composedComponent
        }
        is HorizontalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(0, insertedComponent)
        }
    }
}

fun Component.insertRightComponent(insertedComponent: Component) {
    when (this) {
        is BlockComponent, is VerticalComposedComponent -> {
            val composedComponent = HorizontalComposedComponent(
                components = mutableListOf(this, insertedComponent),
                prevComponent = this.prevComponent,
                nextComponent = this.nextComponent
            )

            replaceItself(composedComponent)

            this.prevComponent = EmptyComponent
            this.nextComponent = EmptyComponent

            this.parentComponent = composedComponent
            insertedComponent.parentComponent = composedComponent
        }
        is HorizontalComposedComponent -> {
            insertedComponent.parentComponent = this
            components.add(insertedComponent)
        }
    }
}

fun Component.removeNextComponent(): Component {
    val removedComponent = nextComponent

    this.nextComponent = removedComponent.nextComponent
    this.nextComponent.prevComponent = this

    removedComponent.prevComponent = EmptyComponent
    removedComponent.nextComponent = EmptyComponent

    return removedComponent
}

fun Component.replaceItself(otherComponent: Component) {
    val convertedParent = parentComponent?.asComposed()
    val hasParent = convertedParent != null

    if (hasParent) {
        convertedParent?.let { parent ->
            this.parentComponent = null
            otherComponent.parentComponent = parent

            val thisIndex = parent.components.indexOf(this)
            parent.components[thisIndex] = otherComponent
        }
    }

    prevComponent.nextComponent = otherComponent
    otherComponent.prevComponent = prevComponent

    nextComponent.prevComponent = otherComponent
    otherComponent.nextComponent = nextComponent
}

fun Component.removeComponent() {
    prevComponent.nextComponent = nextComponent
    nextComponent.prevComponent = prevComponent
}

fun Component.openUp(): List<Component> {
    val openedComponents = mutableListOf<Component>()

    if (this is HorizontalComposedComponent) {
        components.forEach {
            openedComponents.addAll(it.openUp())
        }
    } else {
        openedComponents.add(this)
    }

    return openedComponents
}

// Geometry

fun Component.getDistanceSquaredFromCenter(point: Offset): Float {
    val center = position.plus(Offset(size.width / 2, size.height / 2))

    return (point - center).getDistanceSquared()
}

fun Component.pointInBounds(point: Offset): Boolean {
    val horizontalPadding = if (this is ComposedComponent) HORIZONTAL_COMPONENT_PADDING else 0f
    val verticalPadding = if (this is ComposedComponent) 0f else -MIN_LINE_LENGTH

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
        x0 < x1 && x0 < x2 -> {
            val offsetFromEdge = topLeft.x - pointPosition.x
            ComponentContainment.Left(offsetFromEdge)
        }
        x0 > x1 && x0 < x2 -> {
            val offsetFromEdge = topLeft.y - pointPosition.y
            ComponentContainment.Top(offsetFromEdge)
        }
        x0 > x1 && x0 > x2 -> {
            val offsetFromEdge = pointPosition.x - bottomRight.x
            ComponentContainment.Right(offsetFromEdge)
        }
        else -> {
            val offsetFromEdge = pointPosition.y - topLeft.y
            ComponentContainment.Bottom(offsetFromEdge)
        }
    }
}

fun List<Component>.getClosestToThePoint(point: Offset): Component? {
    if (this.size == 1 && first() is BlockComponent) return first()

    var currentComponent = find { it.pointInBounds(point) }
    var foundComponent = false

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

    return currentComponent

//    val potentialComponent = minByOrNull { it.getDistanceSquaredFromCenter(point) }
//    val isRootParent = potentialComponent is ComposedComponent && potentialComponent.parentComponent == null
//
//    if (potentialComponent is ComposedComponent) {
//        val childPotentialComponent = potentialComponent.components.getClosestToThePoint(point)
//        if (childPotentialComponent != null && childPotentialComponent.pointInBounds(point)) {
//            return childPotentialComponent
//        }
//    }
//
//    return if (isRootParent && potentialComponent?.pointInBounds(point) == true) {
//        (potentialComponent as ComposedComponent).components.getClosestToThePoint(point)
//    } else {
//        potentialComponent
//    }
}

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