package com.letthemcook.recipe.domain.koin

import com.letthemcook.recipe.domain.viewModels.recipe.RecipeViewModel
import com.letthemcook.recipe.domain.viewModels.settings.EditedRecipeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinRecipeModule = module {
    viewModel { RecipeViewModel(get()) }
    viewModel { (recipeId: Int?) -> EditedRecipeViewModel(recipeId) }
}