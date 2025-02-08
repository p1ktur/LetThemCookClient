package com.letthemcook.media.domain.koin

import com.letthemcook.media.domain.viewModels.mediaViewer.MediaViewerViewModel
import com.letthemcook.media.domain.viewModels.mediaViewerImage.MediaViewerImageViewModel
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.domain.model.file.File
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinMediaModule = module {
    viewModel { (file: File, params: RemoteFileManager.RequestParams) ->
        MediaViewerViewModel(file, params, get(), get())
    }
    viewModel { MediaViewerImageViewModel(get()) }
}