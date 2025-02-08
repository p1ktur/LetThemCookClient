package com.letthemcook.media.domain.viewModels.mediaViewer

import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.core.domain.model.file.File

data class MediaViewerUiState(
    val isLoading: Boolean = true,
    val file: File,
    val viewedMediaFile: MediaFile? = null
)
