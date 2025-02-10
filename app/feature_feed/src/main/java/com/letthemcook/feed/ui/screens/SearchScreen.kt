package com.letthemcook.feed.ui.screens

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letthemcook.feed.domain.viewModels.search.SearchUiAction
import com.letthemcook.feed.domain.viewModels.search.SearchUiState
import com.letthemcook.feed.ui.components.CategoriesRadioButtons
import com.letthemcook.feed.ui.components.ProductsRadioButtons
import com.letthemcook.feed.ui.components.RecipeItem
import com.letthemcook.feed.ui.components.UserItem
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.labels.EditedLabelContainer
import com.letthemcook.theme.components.textFields.SearchTextField
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import com.letthemcook.theme.ui.screens.LoadingScreen

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onUiAction: (SearchUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()
            setShowToolBar(false)
            setShowNavigationBar(false)
        }
    }

    val textColor = LocalAppTheme.current.text

    val categoriesFilterNames = remember(uiState.categoriesFilter) { uiState.categoriesFilter.map { it.name } }
    val searchedCategoriesNames = remember(uiState.searchedCategories) { uiState.searchedCategories.map { it.name } }
    val productsFilterNames = remember(uiState.productsFilter) { uiState.productsFilter.map { it.name } }
    val searchedProductsNames = remember(uiState.searchedProducts) { uiState.searchedProducts.map { it.name } }

    val lazyColumnState = rememberLazyListState()

    val isScrolledToBottom by remember {
        derivedStateOf {
            !lazyColumnState.canScrollForward && lazyColumnState.canScrollBackward
        }
    }

    LaunchedEffect(isScrolledToBottom) {
        if (isScrolledToBottom && !uiState.loadingRecipes && uiState.searchText.text.isNotEmpty()) {
            onUiAction(SearchUiAction.LoadNextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
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
                        onUiAction(SearchUiAction.NavigateBack)
                    }
                    .padding(6.dp),
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back Button",
                tint = LocalAppTheme.current.text
            )
            SearchTextField(
                modifier = Modifier.weight(1f),
                state = uiState.searchText
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (uiState.searchClass != SearchUiState.SearchClass.RECIPE) {
                            onUiAction(SearchUiAction.SwitchSearchClass(SearchUiState.SearchClass.RECIPE))
                        }
                    }
                    .padding(8.dp),
                text = "Recipes",
                style = LocalAppTheme.current.typography.titleSmall,
                textDecoration = if (uiState.searchClass == SearchUiState.SearchClass.RECIPE) {
                    TextDecoration.Underline
                } else {
                    null
                },
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (uiState.searchClass != SearchUiState.SearchClass.USER) {
                            onUiAction(SearchUiAction.SwitchSearchClass(SearchUiState.SearchClass.USER))
                        }
                    }
                    .padding(8.dp),
                text = "Users",
                style = LocalAppTheme.current.typography.titleSmall,
                textDecoration = if (uiState.searchClass == SearchUiState.SearchClass.USER) {
                    TextDecoration.Underline
                } else {
                    null
                },
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(color = LocalAppTheme.current.text)
        when (uiState.searchClass) {
            SearchUiState.SearchClass.RECIPE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoriesRadioButtons(
                        modifier = Modifier.fillMaxWidth(),
                        searchType = uiState.recipeSearchType,
                        onRadioButtonSelected = { type ->
                            onUiAction(SearchUiAction.SetSearchType(type))
                        }
                    )
                    HorizontalDivider(color = LocalAppTheme.current.text)
                    ProductsRadioButtons(
                        modifier = Modifier.fillMaxWidth(),
                        sortType = uiState.recipeSortType,
                        onRadioButtonSelected = { type ->
                            onUiAction(SearchUiAction.SetSortType(type))
                        }
                    )
                    HorizontalDivider(color = LocalAppTheme.current.text)
                    EditedLabelContainer(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Filter by categories",
                        labels = categoriesFilterNames,
                        searchTitle = "Categories",
                        searchText = uiState.categoriesSearchText,
                        isLoading = uiState.loadingCategories,
                        searchedLabels = searchedCategoriesNames,
                        onContainerClick = {
                            onUiAction(SearchUiAction.LoadCategories)
                        },
                        onSearchedLabelClick = { index ->
                            onUiAction(SearchUiAction.AddCategory(index))
                        },
                        onSearchedListEndReach = {
                            onUiAction(SearchUiAction.LoadCategories)
                        },
                        onLabelClick = { index ->
                            onUiAction(SearchUiAction.RemoveCategory(index))
                        }
                    )
                    EditedLabelContainer(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Filter by products",
                        labels = productsFilterNames,
                        searchTitle = "Products",
                        searchText = uiState.productsSearchText,
                        isLoading = uiState.loadingProducts,
                        searchedLabels = searchedProductsNames,
                        onContainerClick = {
                            onUiAction(SearchUiAction.LoadProducts)
                        },
                        onSearchedLabelClick = { index ->
                            onUiAction(SearchUiAction.AddProduct(index))
                        },
                        onSearchedListEndReach = {
                            onUiAction(SearchUiAction.LoadProducts)
                        },
                        onLabelClick = { index ->
                            onUiAction(SearchUiAction.RemoveProduct(index))
                        }
                    )
                    if (uiState.loadingRecipes) {
                        LoadingScreen()
                    } else if (uiState.resultsAmount > 0) {
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
                            state = lazyColumnState,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(uiState.searchedRecipes, key = { _, it -> it.id }) { index, recipeItemData ->
                                RecipeItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    recipeItemData = recipeItemData,
                                    index = index,
                                    lastIndex = uiState.searchedRecipes.lastIndex,
                                    onClick = {
                                        onUiAction(SearchUiAction.NavigateToRecipe(recipeItemData.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
            SearchUiState.SearchClass.USER -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.loadingUsers) {
                        LoadingScreen()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp),
                            state = lazyColumnState,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(uiState.searchedUsers, key = { _, it -> it.id }) { index, recipeUserData ->
                                UserItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    userItemData = recipeUserData,
                                    onClick = {
                                        onUiAction(SearchUiAction.NavigateToUser(recipeUserData.id))
                                    }
                                )
                                if (index != uiState.searchedRecipes.lastIndex) {
                                    HorizontalDivider(color = LocalAppTheme.current.text)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}