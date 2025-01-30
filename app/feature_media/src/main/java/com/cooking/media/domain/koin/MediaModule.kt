package com.cooking.media.domain.koin

import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinMediaModule = module {
    viewModel { MediaViewerViewModel(get(), get()) }
}