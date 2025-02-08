package com.letthemcook.profile.domain.viewModels.profile

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.items.RecipeItemData

data class ProfileUiState(
    val user: User? = null,
    val userBitmap: Bitmap? = null,
    val loadingRecipes: Boolean = false,
    val recipes: List<RecipeItemData> = emptyList()
)
