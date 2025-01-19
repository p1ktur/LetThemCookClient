package com.letthemcook.feed.domain.viewModels.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: SearchUiAction) {
        when (action) {
            SearchUiAction.NavigateBack -> Unit
            is SearchUiAction.NavigateToRecipe -> Unit

            is SearchUiAction.SetSearchType -> setSearchType(action.type)
            is SearchUiAction.SetSortType -> setSortType(action.type)
        }
    }

    private fun setSearchType(searchType: SearchUiState.SearchType) {
        _uiState.update {
            it.copy(searchType = searchType)
        }

        //TODO search again
    }

    private fun setSortType(sortType: SearchUiState.SortType) {
        _uiState.update {
            it.copy(sortType = sortType)
        }

        //TODO search again
    }
}