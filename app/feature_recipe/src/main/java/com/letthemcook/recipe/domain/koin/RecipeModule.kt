package com.letthemcook.recipe.domain.koin

import com.letthemcook.recipe.data.LocalReactionsDataManager
import com.letthemcook.recipe.data.database.ReactionsDatabase
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeViewModel
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinRecipeModule = module {
    single { ReactionsDatabase.getInstance(androidContext()) }
    single { get<ReactionsDatabase>().getDao() }
    single { LocalReactionsDataManager(get()) }

    viewModel { RecipeViewModel(get()) }
    viewModel { (recipeId: Int?) -> EditedRecipeViewModel(recipeId) }
}