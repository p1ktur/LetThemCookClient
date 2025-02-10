package com.letthemcook.theme.components.labels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun EditedLabelContainer(
    modifier: Modifier = Modifier,
    name: String,
    labels: List<String>,
    searchTitle: String,
    searchText: TextFieldState,
    isLoading: Boolean,
    searchedLabels: List<String>,
    maxRows: Int = 3,
    onContainerClick: () -> Unit,
    onSearchedLabelClick: (Int) -> Unit,
    onSearchedListEndReach: () -> Unit,
    onLabelClick: (Int) -> Unit,
    onLabelIconClick: ((Int) -> Unit)? = null
) {
    var containerPosition by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var isPopupShown by remember { mutableStateOf(false) }

    Column {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onContainerClick()
                    isPopupShown = true
                }
                .onGloballyPositioned {
                    containerPosition = it.positionInRoot()
                }
                .onSizeChanged {
                    containerSize = it
                }
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Label Icon",
                    tint = LocalAppTheme.current.text
                )
            }
            if (labels.isNotEmpty()) {
                val labelsSize = remember(labels) {
                    if (labels.size > maxRows) maxRows else labels.size
                }

                LazyHorizontalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((labelsSize * 36).dp),
                    rows = StaggeredGridCells.FixedSize(28.dp),
                    horizontalItemSpacing = 8.dp,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(labels) { index, label ->
                        LabelItem(
                            text = label,
                            onClick = {
                                onLabelClick(index)
                            },
                            onIconClick = onLabelIconClick?.let { { it(index) } }
                        )
                    }
                }
            }
        }
        if (isPopupShown) {
            LabelPopup(
                title = searchTitle,
                searchedLabels = searchedLabels,
                searchText = searchText,
                isLoading = isLoading,
                anchorPosition = containerPosition,
                anchorSize = containerSize,
                onSearchedListEndReach = onSearchedListEndReach,
                onLabelClick = onSearchedLabelClick,
                onDismiss = {
                    isPopupShown = false
                }
            )
        }
    }
}