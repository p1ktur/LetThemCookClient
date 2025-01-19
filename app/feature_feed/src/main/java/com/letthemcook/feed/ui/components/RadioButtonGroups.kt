package com.letthemcook.feed.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.unit.dp
import com.letthemcook.feed.domain.viewModels.search.SearchUiState
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.RadioButton

@Composable
fun CategoriesRadioButtons(
    modifier: Modifier = Modifier,
    searchType: SearchUiState.SearchType,
    onRadioButtonSelected: (SearchUiState.SearchType) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                isExpanded = !isExpanded
            }
            .animateContentSize()
    ) {
        if (isExpanded) {
            Column {
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = searchType == SearchUiState.SearchType.NAME,
                    label = "Search by date",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SearchType.NAME)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = searchType == SearchUiState.SearchType.CATEGORY,
                    label = "Search by category",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SearchType.CATEGORY)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = searchType == SearchUiState.SearchType.PRODUCTS,
                    label = "Search by products",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SearchType.PRODUCTS)
                    }
                )
            }
        } else {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterStart),
                text = when (searchType) {
                    SearchUiState.SearchType.NAME -> "Searching by date"
                    SearchUiState.SearchType.CATEGORY -> "Searching by category"
                    SearchUiState.SearchType.PRODUCTS -> "Searching by products"
                },
                style = LocalAppTheme.current.typography.bodyLarge
            )
        }
        Icon(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable {
                    isExpanded = !isExpanded
                }
                .padding(4.dp)
                .align(Alignment.TopEnd),
            imageVector = if (isExpanded) {
                Icons.Default.ExpandLess
            } else {
                Icons.Default.ExpandMore
            },
            contentDescription = "Expand Button",
            tint = LocalAppTheme.current.text
        )
    }
}

@Composable
fun ProductsRadioButtons(
    modifier: Modifier = Modifier,
    sortType: SearchUiState.SortType,
    onRadioButtonSelected: (SearchUiState.SortType) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                isExpanded = !isExpanded
            }
            .animateContentSize()
    ) {
        if (isExpanded) {
            Column {
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.DATE,
                    label = "Sort by date",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.DATE)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.REVIEWS,
                    label = "Sort by reviews",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.REVIEWS)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.PREPARATIONS,
                    label = "Sort by preparations",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.PREPARATIONS)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.LIKES,
                    label = "Sort by likes",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.LIKES)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.POPULARITY,
                    label = "Sort by popularity",
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.POPULARITY)
                    }
                )
            }
        } else {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterStart),
                text = when (sortType) {
                    SearchUiState.SortType.DATE -> "Sorting by date"
                    SearchUiState.SortType.REVIEWS -> "Sorting by reviews"
                    SearchUiState.SortType.PREPARATIONS -> "Sorting by preparations"
                    SearchUiState.SortType.LIKES -> "Sorting by likes"
                    SearchUiState.SortType.POPULARITY -> "Sorting by popularity"
                },
                style = LocalAppTheme.current.typography.bodyLarge
            )
        }
        Icon(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable {
                    isExpanded = !isExpanded
                }
                .padding(4.dp)
                .align(Alignment.TopEnd),
            imageVector = if (isExpanded) {
                Icons.Default.ExpandLess
            } else {
                Icons.Default.ExpandMore
            },
            contentDescription = "Expand Button",
            tint = LocalAppTheme.current.text
        )
    }
}