package com.letthemcook.editor.domain.editor.components.composed

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState

interface ComposedComponent : Component {

    val components: MutableList<Component>

    fun drawOn(
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
    )
}