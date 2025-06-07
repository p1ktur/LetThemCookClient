package com.letthemcook.feed.domain.viewModels.search

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.data.remote.UserManager
import com.letthemcook.core.domain.list.filterOn
import com.letthemcook.theme.providers.LanguageStateProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val recipeManager: RecipeManager,
    private val userManager: UserManager,
    private val languageStateProvider: LanguageStateProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var firstSearch = true

    // Recipes
    private var lastRecipePage = 0
    private var allRecipePagesReached = false
    private var lastRecipeSearchHashCode = 0

    private var lastCategoryPage = 0
    private var allCategoryPagesReached = false
    private var lastCategorySearchText = ""

    private var lastProductPage = 0
    private var allProductPagesReached = false
    private var lastProductSearchText = ""

    // Users
    private var lastUserPage = 0
    private var allUserPagesReached = false
    private var lastUserSearchText = ""

    init {
        viewModelScope.launch {
            snapshotFlow { uiState.value.searchText.text }
                .debounce(500)
                .collectLatest { text ->
                    withContext(Dispatchers.IO) {
                        if (firstSearch) {
                            viewModelScope.launch(Dispatchers.IO) { searchUsers() }
                            viewModelScope.launch(Dispatchers.IO) { searchRecipes() }
                            firstSearch = false
                        } else when (uiState.value.searchClass) {
                            SearchUiState.SearchClass.USER -> searchUsers(text.toString())
                            SearchUiState.SearchClass.RECIPE -> searchRecipes(text.toString())
                        }
                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { uiState.value.categoriesSearchText.text }
                .debounce(500)
                .collectLatest { text ->
                    withContext(Dispatchers.IO) {
                        searchCategories(text.toString())
                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { uiState.value.productsSearchText.text }
                .debounce(500)
                .collectLatest { text ->
                    withContext(Dispatchers.IO) {
                        searchProducts(text.toString())
                    }
                }
        }
    }

    fun onUiAction(action: SearchUiAction) {
        when (action) {
            SearchUiAction.NavigateBack -> Unit
            is SearchUiAction.NavigateToRecipe -> Unit
            is SearchUiAction.NavigateToUser -> Unit

            is SearchUiAction.SetSearchType -> setSearchType(action.type)
            is SearchUiAction.SetSortType -> setSortType(action.type)

            SearchUiAction.LoadCategories -> viewModelScope.launch(Dispatchers.IO) { searchCategories() }
            is SearchUiAction.AddCategory -> addCategory(action.index)
            is SearchUiAction.RemoveCategory -> removeCategory(action.index)

            SearchUiAction.LoadProducts -> viewModelScope.launch(Dispatchers.IO) { searchProducts() }
            is SearchUiAction.AddProduct -> addProduct(action.index)
            is SearchUiAction.RemoveProduct -> removeProduct(action.index)

            SearchUiAction.LoadNextPage -> viewModelScope.launch(Dispatchers.IO) {
                when (uiState.value.searchClass) {
                    SearchUiState.SearchClass.USER -> searchUsers()
                    SearchUiState.SearchClass.RECIPE -> searchRecipes()
                }
            }

            is SearchUiAction.SwitchSearchClass -> switchSearchClass(action.searchClass)
        }
    }

    // Actions

    private fun setSearchType(searchType: SearchUiState.SearchType) {
        _uiState.update {
            it.copy(recipeSearchType = searchType)
        }

        viewModelScope.launch(Dispatchers.IO) {
            searchRecipes()
        }
    }

    private fun setSortType(sortType: SearchUiState.SortType) {
        _uiState.update {
            it.copy(recipeSortType = sortType)
        }

        viewModelScope.launch(Dispatchers.IO) {
            searchRecipes()
        }
    }

    private suspend fun searchCategories(searchText: String? = null) {
        _uiState.update {
            it.copy(
                loadingCategories = true
            )
        }

        val text = searchText ?: uiState.value.categoriesSearchText.text.toString()

        val isFilteringOn = if (lastCategorySearchText != text) {
            lastCategorySearchText = text
            lastCategoryPage = 0
            allCategoryPagesReached = false
            true
        } else {
            if (allCategoryPagesReached) {
                _uiState.update {
                    it.copy(
                        loadingCategories = false
                    )
                }
                return
            }
            false
        }

        val perPage = 20
        val categories = recipeManager.searchCategories(
            page = lastCategoryPage++,
            perPage = perPage,
            language = languageStateProvider.getLastLanguage().toLanguageString(),
            searchText = text
        ).filterNot { uiState.value.categoriesFilter.contains(it) }

        if (categories.size < perPage) allCategoryPagesReached = true

        if (categories.isEmpty()) {
            allCategoryPagesReached = true

            _uiState.update {
                it.copy(
                    searchedCategories = emptyList(),
                    loadingCategories = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    searchedCategories = if (isFilteringOn) {
                        it.searchedCategories.filterOn(categories)
                    } else {
                        it.searchedCategories + categories
                    },
                    loadingCategories = false
                )
            }
        }
    }

    private fun addCategory(index: Int) {
        try {
            if (uiState.value.categoriesFilter.contains(uiState.value.searchedCategories[index])) return

            _uiState.update {
                it.copy(
                    categoriesFilter = it.categoriesFilter + it.searchedCategories[index],
                    searchedCategories = it.searchedCategories - it.searchedCategories[index]
                )
            }
        } catch (_: Exception) {}
    }

    private fun removeCategory(index: Int) {
        try {
            val removedCategory = uiState.value.categoriesFilter[index]

            _uiState.update {
                it.copy(
                    categoriesFilter = it.categoriesFilter - removedCategory,
                    searchedCategories = if (removedCategory.name.contains(uiState.value.categoriesSearchText.text.toString())) {
                        it.searchedCategories + removedCategory
                    } else {
                        it.searchedCategories
                    }
                )
            }
        } catch (_: Exception) {}
    }

    private suspend fun searchProducts(searchText: String? = null) {
        _uiState.update {
            it.copy(
                loadingProducts = true
            )
        }

        val text = searchText ?: uiState.value.productsSearchText.text.toString()

        val isFilteringOn = if (lastProductSearchText != text) {
            lastProductSearchText = text
            lastProductPage = 0
            allProductPagesReached = false
            true
        } else {
            if (allProductPagesReached) {
                _uiState.update {
                    it.copy(
                        loadingProducts = false
                    )
                }
                return
            }
            false
        }

        val perPage = 20
        val products = recipeManager.searchProducts(
            page = lastProductPage++,
            perPage = perPage,
            language = languageStateProvider.getLastLanguage().toLanguageString(),
            searchText = text
        ).filterNot { uiState.value.productsFilter.contains(it) }

        if (products.size < perPage) allProductPagesReached = true

        if (products.isEmpty()) {
            _uiState.update {
                it.copy(
                    searchedProducts = emptyList(),
                    loadingProducts = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    searchedProducts = if (isFilteringOn) {
                        it.searchedProducts.filterOn(products)
                    } else {
                        it.searchedProducts + products
                    },
                    loadingProducts = false
                )
            }
        }
    }

    private fun addProduct(index: Int) {
        try {
            if (uiState.value.productsFilter.contains(uiState.value.searchedProducts[index])) return

            _uiState.update {
                it.copy(
                    productsFilter = it.productsFilter + it.searchedProducts[index],
                    searchedProducts = it.searchedProducts - it.searchedProducts[index]
                )
            }
        } catch (_: Exception) {}
    }

    private fun removeProduct(index: Int) {
        try {
            val removedProduct = uiState.value.productsFilter[index]

            _uiState.update {
                it.copy(
                    productsFilter = it.productsFilter - removedProduct,
                    searchedProducts = if (removedProduct.name.contains(uiState.value.productsSearchText.text.toString())) {
                        it.searchedProducts + removedProduct
                    } else {
                        it.searchedProducts
                    }
                )
            }
        } catch (_: Exception) {}
    }

    private fun switchSearchClass(searchClass: SearchUiState.SearchClass) {
        _uiState.update {
            it.copy(
                searchClass = searchClass
            )
        }

        if (uiState.value.searchText.text.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                when (uiState.value.searchClass) {
                    SearchUiState.SearchClass.USER -> searchUsers()
                    SearchUiState.SearchClass.RECIPE -> searchRecipes()
                }
            }
        }
    }

    // Not actions
    private suspend fun searchRecipes(searchText: String? = null) {
        val text = searchText ?: uiState.value.searchText.text.toString()

        _uiState.update {
            it.copy(
                loadingRecipes = true
            )
        }

        val currentSearchHashCode = uiState.value.searchHashCode()

        val isFilteringOn = if (currentSearchHashCode != lastRecipeSearchHashCode) {
            lastRecipeSearchHashCode = currentSearchHashCode
            lastRecipePage = 0
            allRecipePagesReached = false
            true
        } else {
            if (allRecipePagesReached) {
                _uiState.update {
                    it.copy(
                        loadingRecipes = false
                    )
                }
                return
            }
            false
        }

        val perPage = 10
        val recipes = recipeManager.searchRecipes(
            page = lastRecipePage++,
            perPage = perPage,
            searchText = text,
            searchType = uiState.value.recipeSearchType.value,
            sortType = uiState.value.recipeSortType.value,
            categories = uiState.value.categoriesFilter.map { it.name },
            products = uiState.value.productsFilter.map { it.name }
        )

        if (recipes.size < perPage) allRecipePagesReached = true

        if (recipes.isEmpty()) {
            _uiState.update {
                it.copy(
                    searchedRecipes = emptyList(),
                    resultsAmount = 0,
                    loadingRecipes = false
                )
            }
        } else {
            val newRecipes = if (isFilteringOn) {
                uiState.value.searchedRecipes.filterOn(recipes)
            } else {
                uiState.value.searchedRecipes + recipes
            }

            _uiState.update {
                it.copy(
                    searchedRecipes = newRecipes,
                    resultsAmount = newRecipes.size,
                    loadingRecipes = false
                )
            }
        }
    }

    private suspend fun searchUsers(searchText: String? = null) {
        val text = searchText ?: uiState.value.searchText.text.toString()

        _uiState.update {
            it.copy(
                loadingUsers = true
            )
        }

        val isFilteringOn = if (lastUserSearchText != text) {
            lastUserSearchText = text
            lastUserPage = 0
            allUserPagesReached = false
            true
        } else {
            if (allUserPagesReached) {
                _uiState.update {
                    it.copy(
                        loadingUsers = false
                    )
                }
                return
            }
            false
        }

        val perPage = 20
        val users = userManager.searchUsers(
            page = lastUserPage++,
            perPage = perPage,
            searchText = text,
        )

        if (users.size < perPage) allUserPagesReached = true

        if (users.isEmpty()) {
            _uiState.update {
                it.copy(
                    searchedUsers = emptyList(),
                    loadingUsers = false
                )
            }
        } else {
            val newUsers = if (isFilteringOn) {
                uiState.value.searchedUsers.filterOn(users)
            } else {
                uiState.value.searchedUsers + users
            }

            _uiState.update {
                it.copy(
                    searchedUsers = newUsers,
                    loadingUsers = false
                )
            }
        }
    }
}