package com.letthemcook.feed.domain.koin

import com.letthemcook.feed.domain.viewModels.savedRecipes.SavedRecipesViewModel
import com.letthemcook.feed.domain.viewModels.feed.FeedViewModel
import com.letthemcook.feed.domain.viewModels.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinFeedModule = module {
    viewModel { FeedViewModel() }
    viewModel { SearchViewModel() }
    viewModel { SavedRecipesViewModel() }
}