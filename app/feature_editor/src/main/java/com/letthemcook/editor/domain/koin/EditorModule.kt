package com.letthemcook.editor.domain.koin

import com.letthemcook.editor.data.RecipeLocalDataManager
import com.letthemcook.editor.data.database.BuiltRecipeDatabase
import com.letthemcook.editor.domain.viewModels.builder.BuilderViewModel
import com.letthemcook.editor.domain.viewModels.cooking.CookingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinEditorModule = module {
    single { BuiltRecipeDatabase.getInstance(androidContext()) }
    single { get<BuiltRecipeDatabase>().getDao() }
    single { RecipeLocalDataManager(get()) }

    viewModel { BuilderViewModel() }
    viewModel { CookingViewModel() }
}