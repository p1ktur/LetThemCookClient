package com.letthemcook.recipe.domain.viewModels.editedRecipe

import android.graphics.Bitmap
import androidx.compose.foundation.text.input.TextFieldState
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.recipe.Category
import com.letthemcook.core.domain.model.recipe.Product
import com.letthemcook.core.domain.model.recipe.Recipe
import com.letthemcook.core.domain.model.recipe.WeightedProduct
import com.letthemcook.recipe.domain.viewModels.reviews.LikeStatus
import java.time.LocalDateTime

data class EditedRecipeUiState(
    // Common
    val saveStatus: SaveStatus = SaveStatus.NO_CHANGES,
    // Recipe
    val recipeIsNew: Boolean,
    val recipeId: String,
    val name: TextFieldState = TextFieldState(),
    val description: TextFieldState = TextFieldState(),
    val ownerId: String,
    val authorLogin: String,
    val likesAmount: Int = 0,
    val dislikesAmount: Int = 0,
    val viewsAmount: Int = 0,
    val preparationsAmount: Int = 0,
    val reviewsAmount: Int = 0,
    val cookingTime: Long? = null,
    val recipeJson: String? = null,
    // Categories
    val loadingCategories: Boolean = false,
    val categoriesSearchText: TextFieldState = TextFieldState(),
    val categoriesFilter: List<Category> = emptyList(),
    val searchedCategories: List<Category> = emptyList(),
    // Products
    val loadingProducts: Boolean = false,
    val productsSearchText: TextFieldState = TextFieldState(),
    val productsFilter: List<WeightedProduct> = emptyList(),
    val searchedProducts: List<Product> = emptyList(),
    // Files
    val selectedAttachment: File? = null,
    val attachments: List<File> = emptyList(),
    // Other
    val recipeBitmapId: String? = null,
    val recipeBitmap: Bitmap? = null,
    val likeStatus: LikeStatus = LikeStatus.NONE,
    val publicationDate: LocalDateTime? = null,
    val reviewText: TextFieldState = TextFieldState()
) {
    fun fromRecipe(
        recipe: Recipe,
        bitmap: Bitmap?,
        isLiked: LikeStatus,
        attachments: List<File>
    ): EditedRecipeUiState {
        return copy(
            name = TextFieldState(recipe.name ?: ""),
            description = TextFieldState(recipe.description ?: ""),
            likesAmount = recipe.likesAmount,
            dislikesAmount = recipe.dislikesAmount,
            viewsAmount = recipe.viewsAmount,
            preparationsAmount = recipe.preparationsAmount,
            reviewsAmount = recipe.reviewsAmount,
            cookingTime = recipe.cookingTime,
            recipeJson = recipe.recipeJson,
            categoriesFilter = recipe.categories,
            productsFilter = recipe.products,
            recipeBitmapId = recipe.bitmapId,
            recipeBitmap = bitmap,
            likeStatus = isLiked,
            publicationDate = recipe.publicationDate,
            attachments = attachments
        )
    }

    fun toRecipe(): Recipe {
        return Recipe(
            id = recipeId,
            ownerId = ownerId,
            bitmapId = recipeBitmapId,
            authorLogin = authorLogin,
            name = name.text.toString(),
            description = description.text.toString(),
            cookingTime = cookingTime,
            recipeJson = recipeJson,
            likesAmount = likesAmount,
            dislikesAmount = dislikesAmount,
            viewsAmount = viewsAmount,
            preparationsAmount = preparationsAmount,
            reviewsAmount = reviewsAmount,
            products = productsFilter,
            categories = categoriesFilter,
            attachmentIds = attachments.map { it.uid },
            publicationDate = publicationDate,
            isFavored = false
        )
    }
}
