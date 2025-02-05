package com.letthemcook.profile.domain.viewModels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.data.remote.UserManager
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.file.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    userId: String,
    private val userManager: UserManager,
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

                user.profileBitmapId?.let { fileId ->
                    val params = RemoteFileManager.RequestParams(
                        userId = userId,
                        fileId = fileId,
                        type = FileType.IMAGE
                    )
                    val bytes = remoteFileManager.getFile(params)

                    _uiState.update {
                        it.copy(
                            userBitmap = bytes?.toBitmap()
                        )
                    }
                }
            }

            // TODO Load recipes
        }
    }

    fun onUiAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.NavigateToNewRecipe -> Unit
            ProfileUiAction.NavigateBack -> Unit
            ProfileUiAction.NavigateToHome -> Unit
            ProfileUiAction.NavigateToEditedProfile -> Unit

            is ProfileUiAction.ViewMediaFile -> Unit

            ProfileUiAction.FollowOrUnfollow -> followOrUnfollow()
        }
    }

    private fun followOrUnfollow() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value.user?.let { user ->
                if (user.isFollowed) {
                    userManager.unfollow(user.id)
                } else {
                    userManager.follow(user.id)
                }

                _uiState.update {
                    it.copy(
                        user = it.user?.copy(
                            isFollowed = !user.isFollowed
                        )
                    )
                }
            }
        }
    }
}