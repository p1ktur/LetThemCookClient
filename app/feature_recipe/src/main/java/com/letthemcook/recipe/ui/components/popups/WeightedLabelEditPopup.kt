package com.letthemcook.recipe.ui.components.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.textFields.DigitsTextField
import kotlin.math.roundToInt

@Composable
fun WeightedLabelEditPopup(
    title: String,
    weightText: TextFieldState,
    amountText: TextFieldState,
    anchorPosition: Offset,
    anchorSize: IntSize,
    onAdd: () -> Unit,
    onDismiss: () -> Unit
) {
    val popupPositionProvider = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val delta = 48

                return if (windowSize.height - anchorBounds.height < popupContentSize.height) {
                    IntOffset(
                        x = 0,
                        y = anchorPosition.y.roundToInt() + anchorSize.height - delta
                    )
                } else {
                    IntOffset(
                        x = 0,
                        y = anchorPosition.y.roundToInt() - popupContentSize.height + delta
                    )
                }
            }
        }
    }

    Popup(
        popupPositionProvider = popupPositionProvider,
        properties = PopupProperties(focusable = true),
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LocalAppTheme.current.screenTwo, RoundedCornerShape(12.dp))
                .border(1.dp, LocalAppTheme.current.text, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Add $title",
                style = LocalAppTheme.current.typography.bodyLarge
            )
            DigitsTextField(
                modifier = Modifier.fillMaxWidth(),
                state = weightText,
                labelText = "Weight (g)",
                backgroundColor = LocalAppTheme.current.screenTwo
            )
            DigitsTextField(
                modifier = Modifier.fillMaxWidth(),
                state = amountText,
                labelText = "Amount (pcs)",
                backgroundColor = LocalAppTheme.current.screenTwo
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    text = "Add",
                    onClick = onAdd
                )
            }
        }
    }
}