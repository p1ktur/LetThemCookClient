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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.feed.R
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
                    label = stringResource(R.string.search_by_name),
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SearchType.NAME)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = searchType == SearchUiState.SearchType.CATEGORY,
                    label = stringResource(R.string.search_by_category),
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SearchType.CATEGORY)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = searchType == SearchUiState.SearchType.PRODUCTS,
                    label = stringResource(R.string.search_by_products),
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
                    SearchUiState.SearchType.NAME -> stringResource(R.string.searching_by_name)
                    SearchUiState.SearchType.CATEGORY -> stringResource(R.string.searching_by_category)
                    SearchUiState.SearchType.PRODUCTS -> stringResource(R.string.searching_by_products)
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
                    label = stringResource(R.string.sort_by_date),
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.DATE)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.REVIEWS,
                    label = stringResource(R.string.sort_by_reviews),
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.REVIEWS)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.PREPARATIONS,
                    label = stringResource(R.string.sort_by_preparations),
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.PREPARATIONS)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.LIKES,
                    label = stringResource(R.string.sort_by_likes),
                    onClick = {
                        onRadioButtonSelected(SearchUiState.SortType.LIKES)
                    }
                )
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sortType == SearchUiState.SortType.POPULARITY,
                    label = stringResource(R.string.sort_by_popularity),
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
                    SearchUiState.SortType.DATE -> stringResource(R.string.sorting_by_date)
                    SearchUiState.SortType.REVIEWS -> stringResource(R.string.sorting_by_reviews)
                    SearchUiState.SortType.PREPARATIONS -> stringResource(R.string.sorting_by_preparations)
                    SearchUiState.SortType.LIKES -> stringResource(R.string.sorting_by_likes)
                    SearchUiState.SortType.POPULARITY -> stringResource(R.string.sorting_by_popularity)
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