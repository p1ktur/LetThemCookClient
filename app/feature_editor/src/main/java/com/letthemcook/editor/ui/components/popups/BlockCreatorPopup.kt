package com.letthemcook.editor.ui.components.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.letthemcook.theme.components.textFields.MultiLineTextField
import com.letthemcook.theme.components.textFields.SingleLineTextField
import kotlin.math.roundToInt

@Composable
fun BlockCreatorPopup(
    anchorPosition: Offset,
    anchorSize: IntSize,
    onCreate: (String, String, Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val nameText: TextFieldState = remember { TextFieldState() }
    val descriptionText: TextFieldState = remember { TextFieldState() }
    val timeHoursText: TextFieldState = remember { TextFieldState() }
    val timeMinutesText: TextFieldState = remember { TextFieldState() }
    val timeSecondsText: TextFieldState = remember { TextFieldState() }

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
                text = "Create new block",
                style = LocalAppTheme.current.typography.bodyLarge
            )
            //TODO name cannot be empty so show error
            SingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                state = nameText,
                labelText = "Name",
                backgroundColor = LocalAppTheme.current.screenTwo
            )
            MultiLineTextField(
                modifier = Modifier.fillMaxWidth(),
                state = descriptionText,
                labelText = "Description",
                backgroundColor = LocalAppTheme.current.screenTwo
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DigitsTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    state = timeHoursText,
                    labelText = "Hours",
                    backgroundColor = LocalAppTheme.current.screenTwo
                )
                DigitsTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    state = timeMinutesText,
                    labelText = "Minutes",
                    backgroundColor = LocalAppTheme.current.screenTwo
                )
                DigitsTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    state = timeSecondsText,
                    labelText = "Seconds",
                    backgroundColor = LocalAppTheme.current.screenTwo
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    text = "Create",
                    onClick = {
                        try {
                            onCreate(
                                nameText.toString(),
                                descriptionText.toString(),
                                timeHoursText.toString().toInt(),
                                timeMinutesText.toString().toInt(),
                                timeSecondsText.toString().toInt()
                            )

                            nameText.clearText()
                            descriptionText.clearText()
                            timeHoursText.clearText()
                            timeMinutesText.clearText()
                            timeSecondsText.clearText()
                        } catch (_: Exception) {
                            onDismiss()
                        }
                    }
                )
            }
        }
    }
}