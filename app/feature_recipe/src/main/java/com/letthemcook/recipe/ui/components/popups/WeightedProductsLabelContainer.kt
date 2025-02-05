package com.letthemcook.recipe.ui.components.popups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.letthemcook.core.domain.model.recipe.Product
import com.letthemcook.core.domain.model.recipe.WeightedProduct
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.labels.LabelItem
import com.letthemcook.theme.components.labels.LabelPopup

@Composable
fun WeightedProductsLabelContainer(
    modifier: Modifier = Modifier,
    name: String,
    weightedProducts: List<WeightedProduct>,
    searchTitle: String,
    searchText: TextFieldState,
    isLoading: Boolean,
    searchedProducts: List<Product>,
    maxRows: Int = 3,
    onContainerClick: () -> Unit,
    onLabelCreate: (WeightedProduct) -> Unit,
    onSearchedListEndReach: () -> Unit,
    onLabelClick: (Int) -> Unit
) {
    // TODO delete label
    // TODO edit label
    // TODO when adding label: show edit popup onto this label

    var containerPosition by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var isPopupShown by remember { mutableStateOf(false) }
    var isEditPopupShown by remember { mutableStateOf(false) }

    var chosenProduct: Product? by remember { mutableStateOf(null) }

    val productLabels = remember(searchedProducts) { searchedProducts.map { it.name } }
    val labelsList = remember(weightedProducts) { weightedProducts.map { it.toString() } }

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
            LazyHorizontalStaggeredGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = (maxRows * 36).dp),
                rows = StaggeredGridCells.FixedSize(28.dp),
                horizontalItemSpacing = 8.dp,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(labelsList) { index, label ->
                    LabelItem(
                        text = label,
                        onClick = {
                            onLabelClick(index)
                        }
                    )
                }
            }
        }
        if (isPopupShown) {
            LabelPopup(
                title = searchTitle,
                searchedLabels = productLabels,
                searchText = searchText,
                isLoading = isLoading,
                anchorPosition = containerPosition,
                anchorSize = containerSize,
                onLabelClick = { labelIndex ->
                    chosenProduct = searchedProducts[labelIndex]
                    isPopupShown = false
                    isEditPopupShown = true
                },
                onSearchedListEndReach = onSearchedListEndReach,
                onDismiss = {
                    isPopupShown = false
                }
            )
        }
        if (isEditPopupShown) chosenProduct?.let { chosenProduct ->
            WeightedLabelEditPopup(
                chosenProduct = chosenProduct,
                anchorPosition = containerPosition,
                anchorSize = containerSize,
                onAdd = { weightedProduct ->
                    try {
                        onLabelCreate(weightedProduct)
                    } finally {
                        isPopupShown = true
                        isEditPopupShown = false
                    }
                },
                onDismiss = {
                    isPopupShown = true
                    isEditPopupShown = false
                }
            )
        }
    }
}