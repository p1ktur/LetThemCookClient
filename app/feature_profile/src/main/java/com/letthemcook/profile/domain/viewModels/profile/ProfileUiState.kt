package com.letthemcook.profile.domain.viewModels.profile

import android.graphics.Bitmap
import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.items.RecipeItemData

data class ProfileUiState(
    val user: User? = null,
    val userImage: Bitmap? = null,
    val recipes: List<RecipeItemData> = emptyList()
)
