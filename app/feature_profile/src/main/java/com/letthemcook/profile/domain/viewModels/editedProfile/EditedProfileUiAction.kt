package com.letthemcook.profile.domain.viewModels.editedProfile

import android.graphics.Bitmap

sealed interface EditedProfileUiAction {
    data object NavigateBack : EditedProfileUiAction
    data object NavigateToHome : EditedProfileUiAction
    data object NavigateToNewRecipe : EditedProfileUiAction
    data object NavigateToSettings : EditedProfileUiAction

    data object ChangePassword : EditedProfileUiAction

    data class ViewMediaFile(val bitmap: Bitmap) : EditedProfileUiAction

    data class ViewRecipe(val recipeId: String) : EditedProfileUiAction

    data object UpdateUserData : EditedProfileUiAction
    data class UpdateProfileBitmap(val fileId: String, val bitmap: Bitmap) : EditedProfileUiAction
}