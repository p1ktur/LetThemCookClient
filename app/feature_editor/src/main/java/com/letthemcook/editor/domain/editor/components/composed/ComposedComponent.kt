package com.letthemcook.editor.domain.editor.components.composed

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.editor.domain.editor.components.containment.Relation
import com.letthemcook.editor.domain.editor.components.prototype.Component

interface ComposedComponent : Component {

    val components: MutableList<Component>

    fun drawOn(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        nameTextStyle: TextStyle,
        contentTextStyle: TextStyle,
        frameColor: Color,
        containerColor: Color,
        textColor: Color,
        highlightColor: Color,
        positionXIsCentral: Boolean = false
    )

    fun shadeQuarterForNextFrame(relation: Relation)
}