package com.letthemcook.editor.domain.viewModels.cooking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CookingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CookingUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: CookingUiAction) {
        when (action) {
            CookingUiAction.NavigateBack -> Unit
            is CookingUiAction.NavigateToRecipe -> Unit

            is CookingUiAction.SetSearchType -> setSearchType(action.type)
            is CookingUiAction.SetSortType -> setSortType(action.type)
        }
    }

    private fun setSearchType(searchType: CookingUiState.SearchType) {
        _uiState.update {
            it.copy(searchType = searchType)
        }

        //TODO search again
    }

    private fun setSortType(sortType: CookingUiState.SortType) {
        _uiState.update {
            it.copy(sortType = sortType)
        }

        //TODO search again
    }
}