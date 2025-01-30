package com.cooking.media.domain.viewModels.mediaViewer

import androidx.lifecycle.ViewModel
import com.letthemcook.core.domain.model.data.file.MediaFile
import com.letthemcook.core.data.files.FilesManager
import com.letthemcook.core.domain.model.data.file.File
import com.letthemcook.core.domain.model.data.file.FileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MediaViewerViewModel(
    file: File,
    filesManager: FilesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaViewerUiState(file))
    val uiState = _uiState.asStateFlow()

    init {
        when (file.type) {
            FileType.IMAGE -> {
                filesManager.getFileAsBitmap(file)?.let { bitmap ->
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

    fun onUiAction(action: MediaViewerUiAction) {
        when (action) {
            MediaViewerUiAction.NavigateBack -> Unit
        }
    }
}