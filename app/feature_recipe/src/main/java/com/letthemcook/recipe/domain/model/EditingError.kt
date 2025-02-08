package com.letthemcook.recipe.domain.model

sealed interface EditingError {
    data object EmptyName : EditingError
    data object TooManyCategories : EditingError
    data object TooManyProducts : EditingError
}