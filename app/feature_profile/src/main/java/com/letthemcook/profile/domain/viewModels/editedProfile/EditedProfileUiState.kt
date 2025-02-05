package com.letthemcook.profile.domain.viewModels.editedProfile

import android.graphics.Bitmap
import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.items.RecipeItemData

data class EditedProfileUiState(
    // User
    val user: User,
    val userBitmap: Bitmap? = null,
    // Edit Fields
    val about: TextFieldState = TextFieldState(user.about ?: ""),
    val name: TextFieldState = TextFieldState(user.name ?: ""),
    val surname: TextFieldState = TextFieldState(user.surname ?: ""),
    val email: TextFieldState = TextFieldState(user.email),
    val phoneNumber: TextFieldState = TextFieldState(user.phone ?: ""),
    // Other
    val recipes: List<RecipeItemData> = emptyList()
)
