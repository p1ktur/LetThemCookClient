package com.letthemcook.editor.domain.editor.geometry

import androidx.compose.ui.geometry.Offset
import com.letthemcook.editor.domain.editor.components.BlockComponent
import com.letthemcook.editor.domain.editor.components.containment.ComponentContainment
import com.letthemcook.editor.ui.drawing.DRAW_PADDING
import com.letthemcook.editor.ui.drawing.MIN_LINE_LENGTH

fun BlockComponent.checkForContainment(mousePosition: Offset): ComponentContainment {
//    val containmentRadius = 16f
//
//    val containsLeft = mousePosition.x in (position.x - containmentRadius)..(position.x + containmentRadius) &&
//            mousePosition.y in (position.y + containmentRadius)..(position.y + size.height - containmentRadius)
//
//    if (containsLeft) return ComponentContainmentResult.Side(SideDirection.LEFT)
//
//    val containsTop = mousePosition.x in (position.x + containmentRadius)..(position.x + size.width - containmentRadius) &&
//            mousePosition.y in (position.y - containmentRadius)..(position.y + containmentRadius)
//
//    if (containsTop) return ComponentContainmentResult.Side(SideDirection.TOP)
//
//    val containsRight = mousePosition.x in (position.x + size.width - containmentRadius)..(position.x + size.width + containmentRadius) &&
//            mousePosition.y in (position.y + containmentRadius)..(position.y + size.height - containmentRadius)
//
//    if (containsRight) return ComponentContainmentResult.Side(SideDirection.RIGHT)
//
//    val containsBottom = mousePosition.x in (position.x + containmentRadius)..(position.x + size.width - containmentRadius) &&
//            mousePosition.y in (position.y + size.height - containmentRadius)..(position.y + size.height + containmentRadius)
//
//    if (containsBottom) return ComponentContainmentResult.Side(SideDirection.BOTTOM)

    if (productNames.isNotEmpty()) {
        var productCurrentPosition = position.plus(Offset(size.width - DRAW_PADDING * 2, DRAW_PADDING))

        for (index in productNames.indices) {
            val widthRange = productCurrentPosition.x..productCurrentPosition.x + cachedDrawnProductLabelSizes[index].width + DRAW_PADDING * 4
            val heightRange = productCurrentPosition.y..productCurrentPosition.y + cachedDrawnProductLabelSizes[index].height + DRAW_PADDING * 2

            val containsWhole = mousePosition.x in widthRange && mousePosition.y in heightRange

            if (containsWhole) {
                return ComponentContainment.ProductLabel(index)
            }

            productCurrentPosition += Offset(0f, DRAW_PADDING * 4 + cachedDrawnProductLabelSizes[index].height)
        }
    }


    val containsWhole = mousePosition.x in (position.x..position.x + size.width) &&
            mousePosition.y in (position.y + MIN_LINE_LENGTH..position.y + size.height - MIN_LINE_LENGTH)

    return if (containsWhole) {
        ComponentContainment.Whole
    } else {
        ComponentContainment.None
    }
}