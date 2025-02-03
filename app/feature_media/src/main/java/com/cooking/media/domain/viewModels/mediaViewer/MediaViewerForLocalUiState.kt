package com.cooking.media.domain.viewModels.mediaViewer

import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.core.domain.model.file.File

data class MediaViewerForLocalUiState(
    val file: File,
    val viewedMediaFile: MediaFile? = null
)
