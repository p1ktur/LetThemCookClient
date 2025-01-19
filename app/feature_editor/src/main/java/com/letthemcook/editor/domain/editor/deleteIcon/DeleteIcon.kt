package com.letthemcook.editor.domain.editor.deleteIcon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiState

data class DeleteIcon(
    val icon: VectorPainter,
    var size: Size,
    var position: Offset
) {
    companion object {
        @Composable
        fun rememberDeleteIcon(uiState: BuilderUiState): DeleteIcon {
            val density = LocalDensity.current

            val deleteIconSize = remember { Size(36f * density.density, 36f * density.density) }
            val deleteIconPosition = remember(uiState.canvasUiState.size) {
                Offset(uiState.canvasUiState.size.width - deleteIconSize.width - 16f, 16f)
            }
            return DeleteIcon(
                icon = rememberVectorPainter(Icons.Outlined.Remove),
                size = deleteIconSize,
                position = deleteIconPosition
            )
        }
    }

    fun drawOn(
        drawScope: DrawScope,
        contentColor: Color,
        containerColor: Color,
        alpha: Float
    ) = drawScope.run {
        translate(top = position.y, left = position.x) {
            drawCircle(
                center = Offset(this@DeleteIcon.size.width / 2, this@DeleteIcon.size.height / 2),
                color = containerColor.copy(alpha = alpha),
                radius = this@DeleteIcon.size.width / 2
            )
            drawCircle(
                center = Offset(this@DeleteIcon.size.width / 2, this@DeleteIcon.size.height / 2),
                color = contentColor.copy(alpha = alpha),
                radius = this@DeleteIcon.size.width / 2,
                style = Stroke(2f)
            )
            translate(top = this@DeleteIcon.size.height * 0.125f, left = this@DeleteIcon.size.width * 0.125f) {
                with (icon) {
                    draw(
                        size = this@DeleteIcon.size.times(0.75f),
                        alpha = alpha,
                        colorFilter = ColorFilter.tint(contentColor, BlendMode.SrcAtop)
                    )
                }
            }
        }
    }
}
