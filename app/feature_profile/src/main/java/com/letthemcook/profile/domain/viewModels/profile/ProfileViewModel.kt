package com.letthemcook.profile.domain.viewModels.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.remote.file.RemoteFileManager
import com.letthemcook.core.data.remote.user.UserManager
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.file.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    userId: String,
    userManager: UserManager,
    remoteFileManager: RemoteFileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userManager.getUser(userId)?.let { user ->
                _uiState.update {
                    it.copy(
                        user = user
                    )
                }

                user.profilePictureId?.let { fileId ->
                    val params = RemoteFileManager.RequestParams(
                        userId = userId,
                        fileId = fileId,
                        type = FileType.IMAGE
                    )
                    val bytes = remoteFileManager.getFile(params)

                    _uiState.update {
                        it.copy(
                            userImage = bytes?.toBitmap()
                        )
                    }
                }
            }

            // TODO Load recipes
        }
    }

    fun onUiAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.NavigateToAddRecipe -> Unit
            ProfileUiAction.NavigateBack -> Unit
            ProfileUiAction.NavigateToHome -> Unit
            ProfileUiAction.NavigateToEditedProfile -> Unit

            is ProfileUiAction.ViewMediaFile -> Unit
        }
    }
}