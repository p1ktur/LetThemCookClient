package com.cooking.media.domain.viewModels.mediaViewerImage

import androidx.lifecycle.ViewModel
import com.letthemcook.core.domain.media.toBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaViewerImageViewModel(
    bitmapBytes: ByteArray
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaViewerImageUiState(bitmapBytes.toBitmap()))
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: MediaViewerImageUiAction) {
        when (action) {
            MediaViewerImageUiAction.NavigateBack -> Unit
        }
    }
}