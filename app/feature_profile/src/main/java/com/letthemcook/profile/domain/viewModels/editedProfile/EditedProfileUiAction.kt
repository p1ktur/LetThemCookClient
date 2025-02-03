package com.letthemcook.profile.domain.viewModels.editedProfile

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.file.File

sealed interface EditedProfileUiAction {
    data object NavigateBack : EditedProfileUiAction
    data object NavigateToHome : EditedProfileUiAction
    data object NavigateToAddRecipe : EditedProfileUiAction
    data object NavigateToSettings : EditedProfileUiAction

    data object ChangePassword : EditedProfileUiAction

    data class ViewMediaFile(val file: File) : EditedProfileUiAction

    data class ViewRecipe(val recipeId: String) : EditedProfileUiAction

    data object UpdateUserData : EditedProfileUiAction
    data class UpdateProfilePicture(val fileId: String, val bitmap: Bitmap) : EditedProfileUiAction
}