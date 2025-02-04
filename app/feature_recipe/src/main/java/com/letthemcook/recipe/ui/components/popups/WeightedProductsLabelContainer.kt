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
import androidx.compose.foundation.text.input.clearText
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
import com.letthemcook.recipe.domain.model.data.WeightedProductItemData
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.labels.LabelItem
import com.letthemcook.theme.components.labels.LabelPopup

@Composable
fun WeightedProductsLabelContainer(
    modifier: Modifier = Modifier,
    name: String,
    productDataList: List<WeightedProductItemData>,
    searchTitle: String,
    searchText: TextFieldState,
    searchedLabels: List<Product>,
    maxRows: Int = 3,
    onLabelCreate: (WeightedProductItemData) -> Unit,
    onContainerClick: () -> Unit,
    onLabelClick: (Int) -> Unit
) {
    // TODO delete label
    // TODO edit label
    // TODO when adding label: show edit popup onto this label

    var containerPosition by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var isPopupShown by remember { mutableStateOf(false) }
    var isEditPopupShown by remember { mutableStateOf(false) }

    var chosenProductItem: Product? by remember { mutableStateOf(null) }
    val weightText = remember { TextFieldState() }
    val amountText = remember { TextFieldState() }

    val labelsList = remember(productDataList) { productDataList.map { it.toString() } }

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
            searchedLabels = searchedLabels.map { it.name }, // TODO optimize
            searchText = searchText,
            isLoading = false, // TODO
            anchorPosition = containerPosition,
            anchorSize = containerSize,
            onLabelClick = { labelIndex ->
                chosenProductItem = searchedLabels[labelIndex]
                isPopupShown = false
                isEditPopupShown = true
            },
            onSearchedListEndReach = {
                //TODO
            },
            onDismiss = {
                isPopupShown = false
            }
        )
    }

    if (isEditPopupShown) {
        WeightedLabelEditPopup(
            title = chosenProductItem?.name ?: "",
            weightText = weightText,
            amountText = amountText,
            anchorPosition = containerPosition,
            anchorSize = containerSize,
            onAdd = {
                try {
                    val data = WeightedProductItemData(
                        chosenProductItem ?: return@WeightedLabelEditPopup,
                        weightText.text.toString().toInt(),
                        amountText.text.toString().toInt()
                    )
                    onLabelCreate(data)

                    weightText.clearText()
                    amountText.clearText()
                    isEditPopupShown = false
                } catch (_: Exception) {

                }
            },
            onDismiss = {
                isEditPopupShown = false
            }
        )
    }
}