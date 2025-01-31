package com.letthemcook.editor.domain.koin

import com.letthemcook.editor.domain.serialization.RecipeGraphSerializer
import com.letthemcook.editor.domain.viewModels.builder.BuilderViewModel
import com.letthemcook.editor.domain.viewModels.cooking.CookingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinEditorModule = module {
    single { RecipeGraphSerializer(get()) }

    viewModel { BuilderViewModel(get()) }
    viewModel { CookingViewModel(get()) }
}