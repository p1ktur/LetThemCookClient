package com.letthemcook.profile.domain.viewModels.editedProfile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.data.remote.UserManager
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.extensions.compressBitmap
import com.letthemcook.core.domain.model.file.extensions.toBitmap
import com.letthemcook.core.domain.model.remote.reactions.toLikeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditedProfileViewModel(
    private val user: User,
    private val authManager: AuthManager,
    private val userManager: UserManager,
    private val remoteFileManager: RemoteFileManager,
    private val localFileManager: LocalFileManager,
    private val localDataManager: LocalDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditedProfileUiState(user = user))
    val uiState = _uiState.asStateFlow()

    fun onUiAction(action: EditedProfileUiAction) {
        when (action) {
            EditedProfileUiAction.NavigateToNewRecipe -> Unit
            EditedProfileUiAction.NavigateBack -> Unit
            EditedProfileUiAction.NavigateToHome -> Unit
            EditedProfileUiAction.NavigateToSettings -> Unit

            is EditedProfileUiAction.NavigateToRecipe -> Unit
            is EditedProfileUiAction.NavigateToEditRecipe -> Unit

            EditedProfileUiAction.NavigateToChangePassword -> Unit

            is EditedProfileUiAction.ViewMediaFile -> Unit

            EditedProfileUiAction.LoadRecipes -> loadRecipes()

            is EditedProfileUiAction.SetBirthDate -> setBirthDate(action.date)

            EditedProfileUiAction.UpdateUserData -> updateProfileData()
            is EditedProfileUiAction.UpdateProfileBitmap -> updateProfileBitmap(action.fileId, action.bitmap)
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            val allRecipes = localDataManager.getRecipes().map { recipe ->
                val recipeBitmap = recipe.bitmapId?.let { fileId ->
                    val localFile = localFileManager.getFileByUid(fileId)

                    if (localFile != null) {
                        localFileManager.getFileBytes(localFile)?.toBitmap()
                    } else {
                        val params = RemoteFileManager.RequestParams(
                            userId = uiState.value.user.id,
                            fileId = fileId,
                            recipeId = recipe.id,
                            type = FileType.IMAGE
                        )
                        val remoteFile = remoteFileManager.getFile(params)

                        if (remoteFile != null) {
                            localFileManager.saveFile(remoteFile, FileType.IMAGE, fileId)
                        }

                        remoteFile?.toBitmap()
                    }
                }

                val likeStatus = localDataManager.getRecipeReaction(recipe.id).toLikeStatus()

                recipe.asItemData(recipeBitmap, likeStatus)
            }

            _uiState.update { state ->
                state.copy(
                    publishedRecipes = allRecipes.filter { it.publicationDate != null },
                    archivedRecipes = allRecipes.filter { it.publicationDate == null }
                )
            }

            userManager.getUser(user.id)?.let { remoteData ->
                val updatedUser = uiState.value.user.copy(
                    totalRecipes = remoteData.totalRecipes,
                    totalPreparations = remoteData.totalPreparations,
                    totalFollowers = remoteData.totalFollowers,
                    averageRating = remoteData.averageRating
                )

                authManager.setUser(updatedUser)
            }
        }
    }

    private fun setBirthDate(date: LocalDateTime) {
        _uiState.update {
            it.copy(
                birthDate = date
            )
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
                    birthDate = it.birthDate
                )
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (userManager.updateUser(uiState.value.user)) {
                authManager.setUser(uiState.value.user)
            }
        }
    }

    private fun updateProfileBitmap(fileId: String, inBitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = if (inBitmap == null) {
                val params = RemoteFileManager.RequestParams(
                    userId = uiState.value.user.id,
                    fileId = fileId,
                    type = FileType.IMAGE
                )

                remoteFileManager.getFile(params)?.apply {
                    val localFile = localFileManager.getFileByUid(fileId)
                    if (localFile == null) {
                        localFileManager.saveFile(this, FileType.IMAGE, fileId)
                    } else {
                        localFileManager.updateFile(localFile, this)
                    }
                }?.toBitmap()
            } else {
                inBitmap
            }

            if (bitmap == null) return@launch

            if (uiState.value.user.profileBitmapId == null || uiState.value.userBitmap == null) {
                _uiState.update {
                    it.copy(
                        user = it.user.copy(profileBitmapId = fileId),
                        userBitmap = bitmap
                    )
                }
            }

            val params = RemoteFileManager.RequestParams(
                userId = uiState.value.user.id,
                fileId = fileId,
                type = FileType.IMAGE
            )

            if (inBitmap != null && !remoteFileManager.uploadFile(params, bitmap.compressBitmap())) return@launch
            if (!userManager.updateUser(uiState.value.user)) return@launch

            authManager.setUser(uiState.value.user)
        }
    }
}