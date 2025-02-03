package com.letthemcook.recipe.domain.koin

import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeViewModel
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinRecipeModule = module {
    viewModel { RecipeViewModel(get()) }
    viewModel { (recipeId: String?) -> EditedRecipeViewModel(recipeId) }
}