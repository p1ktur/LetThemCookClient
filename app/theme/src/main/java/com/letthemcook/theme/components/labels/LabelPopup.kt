package com.letthemcook.theme.components.labels

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.CircularProgressIndicator
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
import com.letthemcook.theme.components.textFields.SearchTextField
import kotlin.math.roundToInt

@Composable
fun LabelPopup(
    title: String,
    searchedLabels: List<String>,
    searchText: TextFieldState,
    isLoading: Boolean,
    anchorPosition: Offset,
    anchorSize: IntSize,
    onSearchedListEndReach: () -> Unit,
    onLabelClick: (Int) -> Unit,
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

                return if (anchorBounds.top < windowSize.height / 2) {
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
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                state = searchText
            )
            LabelContainer(
                modifier = Modifier.fillMaxWidth(),
                name = title,
                labels = searchedLabels,
                isLoading = isLoading,
                onScrolledToEnd = onSearchedListEndReach,
                onLabelClick = onLabelClick
            )
        }
    }
}