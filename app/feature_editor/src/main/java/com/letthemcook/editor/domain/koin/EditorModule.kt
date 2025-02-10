package com.letthemcook.editor.domain.koin

import com.letthemcook.core.data.local.UnusedBlocksDaoDeleter
import com.letthemcook.core.domain.model.remote.WeightedProduct
import com.letthemcook.editor.data.UnusedBlocksDao
import com.letthemcook.editor.data.UnusedBlocksDatabase
import com.letthemcook.editor.domain.serialization.RecipeGraphSerializer
import com.letthemcook.editor.domain.viewModels.builder.BuilderViewModel
import com.letthemcook.editor.domain.viewModels.cooking.CookingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinEditorModule = module {
    single { UnusedBlocksDatabase.getInstance(androidContext()) }
    single { get<UnusedBlocksDatabase>().getDao() }
    single { get<UnusedBlocksDao>() as UnusedBlocksDaoDeleter } // DO NOT REMOVE CAST BECAUSE OF HOW KOIN WORKS

    single { RecipeGraphSerializer(get()) }

    viewModel { (ownerId: String, recipeId: String, recipeJson: String?, recipeName: String, products: List<WeightedProduct>) ->
        BuilderViewModel(ownerId, recipeId, recipeJson, recipeName, products, get(), get(), get())
    }
    viewModel { CookingViewModel(get(), get(), get(), get(), get(), get(), get()) }
}