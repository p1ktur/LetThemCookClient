package com.letthemcook.profile.domain.viewModels.editedProfile

import android.graphics.Bitmap
import java.time.LocalDateTime

sealed interface EditedProfileUiAction {
    data object NavigateBack : EditedProfileUiAction
    data object NavigateToHome : EditedProfileUiAction
    data object NavigateToNewRecipe : EditedProfileUiAction
    data object NavigateToSettings : EditedProfileUiAction

    data class NavigateToRecipe(val recipeId: String) : EditedProfileUiAction
    data class NavigateToEditRecipe(val recipeId: String) : EditedProfileUiAction

    data object NavigateToChangePassword : EditedProfileUiAction

    data class ViewMediaFile(val bitmap: Bitmap) : EditedProfileUiAction

    data object LoadRecipes : EditedProfileUiAction

    data class SetBirthDate(val date: LocalDateTime) : EditedProfileUiAction

    data object UpdateUserData : EditedProfileUiAction
    data class UpdateProfileBitmap(val fileId: String, val bitmap: Bitmap?) : EditedProfileUiAction
}