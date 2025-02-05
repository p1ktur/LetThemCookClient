package com.cooking.media.domain.viewModels.mediaViewer

import androidx.lifecycle.ViewModel
import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MediaViewerForLocalViewModel(
    file: File,
    localFileManager: LocalFileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaViewerForLocalUiState(file))
    val uiState = _uiState.asStateFlow()

    init {
        when (file.type) {
            FileType.IMAGE -> {
                localFileManager.getFileBytes(file)?.toBitmap()?.let { bitmap ->
                    _uiState.update {
                        it.copy(
                            viewedMediaFile = MediaFile.Image(
                                bitmap = bitmap,
                                file = file
                            )
                        )
                    }
                }
            }
            FileType.VIDEO -> {
                _uiState.update {
                    it.copy(
                        viewedMediaFile = MediaFile.Video(
                            file = file
                        )
                    )
                }
            }
            FileType.ANY -> Unit
        }
    }

    fun onUiAction(action: MediaViewerForLocalUiAction) {
        when (action) {
            MediaViewerForLocalUiAction.Close -> Unit
        }
    }
}