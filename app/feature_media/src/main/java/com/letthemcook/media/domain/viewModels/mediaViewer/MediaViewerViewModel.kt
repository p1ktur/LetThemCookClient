package com.letthemcook.media.domain.viewModels.mediaViewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaViewerViewModel(
    file: File,
    params: RemoteFileManager.RequestParams?,
    localFileManager: LocalFileManager,
    remoteFileManager: RemoteFileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaViewerUiState(file = file))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            when (file.type) {
                FileType.IMAGE -> {
                    val localBytes = localFileManager.getFileBytes(file)

                    if (localBytes != null) {
                        _uiState.update {
                            it.copy(
                                viewedMediaFile = MediaFile.Image(
                                    bitmap = localBytes.toBitmap(),
                                    file = file
                                )
                            )
                        }
                    } else if (params != null) {
                        val remoteBytes = remoteFileManager.getFile(params)

                        if (remoteBytes != null) {
                            _uiState.update {
                                it.copy(
                                    viewedMediaFile = MediaFile.Image(
                                        bitmap = remoteBytes.toBitmap(),
                                        file = file
                                    )
                                )
                            }
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

            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun onUiAction(action: MediaViewerUiAction) {
        when (action) {
            MediaViewerUiAction.Close -> Unit
        }
    }
}