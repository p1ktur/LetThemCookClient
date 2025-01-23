package com.letthemcook.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiAction
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiState
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.labels.EditedLabelContainer
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import com.letthemcook.theme.components.textFields.SearchTextField

@Composable
fun CookingScreen(
    uiState: CookingUiState,
    onUiAction: (CookingUiAction) -> Unit
) {
    val textColor = LocalAppTheme.current.text

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(LocalAppTheme.current.screenThree)
                .drawBehind {
                    drawLine(
                        color = textColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height)
                    )
                }
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onUiAction(CookingUiAction.NavigateBack)
                    }
                    .padding(6.dp),
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back Button",
                tint = LocalAppTheme.current.text
            )
            SearchTextField(
                modifier = Modifier.weight(1f),
                state = uiState.searchText,
                onSearchClick = {

                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            HorizontalDivider(color = LocalAppTheme.current.text)
            EditedLabelContainer(
                modifier = Modifier.fillMaxWidth(),
                name = "Filter by categories",
                labels = uiState.categoriesFilter.map { it.name }, // TODO optimize!!!
                searchTitle = "Categories",
                searchText = uiState.categoriesSearchText,
                searchedLabels = uiState.searchedCategories.map { it.name },
                onContainerClick = {},
                onLabelClick = {}
            )
            EditedLabelContainer(
                modifier = Modifier.fillMaxWidth(),
                name = "Filter by products",
                labels = uiState.productsFilter.map { it.name }, // TODO optimize!!!
                searchTitle = "Products",
                searchText = uiState.productsSearchText,
                searchedLabels = uiState.searchedProducts.map { it.name },
                onContainerClick = {},
                onLabelClick = {}
            )
            if (uiState.resultsAmount > 0) {
                HorizontalDivider(color = LocalAppTheme.current.text)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Results: ${uiState.resultsAmount}",
                    style = LocalAppTheme.current.typography.titleMedium,
                    color = LocalAppTheme.current.text,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(color = LocalAppTheme.current.text)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = "Results: ${uiState.resultsAmount}",
                            style = LocalAppTheme.current.typography.bodyLarge
                        )
                    }
                    itemsIndexed(uiState.searchedRecipes, key = { _, it -> it.id }) { index, recipeItemData ->

                        if (index != uiState.searchedRecipes.lastIndex) {
                            HorizontalDivider(color = LocalAppTheme.current.text)
                        }
                    }
                }
            }
        }
        BottomInsetSpacer()
    }

    // TODO button to scroll to top when lazyColumn is scrolled
}