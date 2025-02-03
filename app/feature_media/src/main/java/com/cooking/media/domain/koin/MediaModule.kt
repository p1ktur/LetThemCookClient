package com.cooking.media.domain.koin

import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerForLocalViewModel
import com.cooking.media.domain.viewModels.mediaViewerImage.MediaViewerImageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinMediaModule = module {
    viewModel { MediaViewerForLocalViewModel(get(), get()) }
    viewModel { MediaViewerImageViewModel(get()) }
}