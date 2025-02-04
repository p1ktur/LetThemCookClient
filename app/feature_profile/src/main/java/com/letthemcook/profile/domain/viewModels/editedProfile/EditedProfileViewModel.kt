package com.letthemcook.profile.domain.viewModels.editedProfile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.data.remote.UserManager
import com.letthemcook.core.domain.media.compressBitmap
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.file.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditedProfileViewModel(
    user: User,
    private val authManager: AuthManager,
    private val userManager: UserManager,
    private val remoteFileManager: RemoteFileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditedProfileUiState(user = user))
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: EditedProfileUiAction) {
        when (action) {
            EditedProfileUiAction.NavigateToAddRecipe -> Unit
            EditedProfileUiAction.NavigateBack -> Unit
            EditedProfileUiAction.NavigateToHome -> Unit
            EditedProfileUiAction.NavigateToSettings -> Unit

            is EditedProfileUiAction.ViewMediaFile -> Unit

            is EditedProfileUiAction.ViewRecipe -> Unit

            EditedProfileUiAction.ChangePassword -> Unit

            EditedProfileUiAction.UpdateUserData -> updateProfileData()
            is EditedProfileUiAction.UpdateProfilePicture -> updateProfilePicture(action.fileId, action.bitmap)
        }
    }

    private fun updateProfileData() {
        _uiState.update {
            it.copy(
                user = it.user.copy(
                    about = it.about.text.toString(),
                    name = it.name.text.toString(),
                    surname = it.surname.text.toString(),
                    email = it.email.text.toString(),
                    phone = it.phoneNumber.text.toString(),
                )
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (userManager.updateUser(uiState.value.user)) {
                authManager.setUser(uiState.value.user)
            }
        }
    }

    private fun updateProfilePicture(fileId: String, bitmap: Bitmap) {
        if (uiState.value.user.profilePictureId == null) {
            _uiState.update {
                it.copy(
                    user = it.user.copy(profilePictureId = fileId)
                )
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val params = RemoteFileManager.RequestParams(
                userId = uiState.value.user.id,
                fileId = fileId,
                type = FileType.IMAGE
            )

            if (!remoteFileManager.uploadFile(params, bitmap.compressBitmap())) return@launch
            if (!userManager.updateUser(uiState.value.user)) return@launch

            authManager.setUser(uiState.value.user)
        }
    }
}