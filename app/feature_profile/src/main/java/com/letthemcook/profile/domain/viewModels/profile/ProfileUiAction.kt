package com.letthemcook.profile.domain.viewModels.profile

import android.graphics.Bitmap

sealed interface ProfileUiAction {
    data object NavigateBack : ProfileUiAction
    data object NavigateToHome : ProfileUiAction
    data object NavigateToNewRecipe : ProfileUiAction
    data object NavigateToEditedProfile : ProfileUiAction

    data class ViewMediaFile(val bitmap: Bitmap) : ProfileUiAction

    data object FollowOrUnfollow : ProfileUiAction
}