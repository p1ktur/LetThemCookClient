package com.letthemcook.recipe.domain.viewModels.recipe

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.items.ReviewItemData
import com.letthemcook.core.domain.model.remote.Category
import com.letthemcook.core.domain.model.remote.Recipe
import com.letthemcook.core.domain.model.remote.WeightedProduct
import com.letthemcook.core.domain.model.status.LikeStatus
import com.letthemcook.recipe.domain.model.LoadingStatus
import java.time.LocalDateTime

data class RecipeUiState(
    // Recipe
    val isOwner: Boolean = false,
    val loadingStatus: LoadingStatus = LoadingStatus.LOADING,
    val recipeId: String,
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val authorLogin: String = "",
    val likesAmount: Int = 0,
    val dislikesAmount: Int = 0,
    val viewsAmount: Int = 0,
    val preparationsAmount: Int = 0,
    val reviewsAmount: Int = 0,
    val cookingTime: Long? = null,
    val recipeJson: String? = null,
    // Categories
    val categories: List<Category> = emptyList(),
    // Products
    val products: List<WeightedProduct> = emptyList(),
    // Files
    val attachments: List<File> = emptyList(),
    // Reviews
    val loadingReviews: Boolean = false,
    val reviews: List<ReviewItemData> = emptyList(),
    // Other
    val recipeBitmapId: String? = null,
    val recipeBitmap: Bitmap? = null,
    val likeStatus: LikeStatus = LikeStatus.NONE,
    val isFavored: Boolean = false,
    val publicationDate: LocalDateTime? = null
) {
    fun fromRecipe(
        recipe: Recipe,
        bitmap: Bitmap?,
        isLiked: LikeStatus,
        attachments: List<File>,
        isFavored: Boolean
    ): RecipeUiState {
        return copy(
            name = recipe.name ?: "",
            description = recipe.description ?: "",
            ownerId = recipe.ownerId,
            authorLogin = recipe.authorLogin,
            likesAmount = recipe.likesAmount,
            dislikesAmount = recipe.dislikesAmount,
            viewsAmount = recipe.viewsAmount,
            preparationsAmount = recipe.preparationsAmount,
            reviewsAmount = recipe.reviewsAmount,
            cookingTime = recipe.cookingTime,
            recipeJson = recipe.recipeJson,
            categories = recipe.categories,
            products = recipe.products,
            recipeBitmapId = recipe.bitmapId,
            recipeBitmap = bitmap,
            likeStatus = isLiked,
            publicationDate = recipe.publicationDate,
            attachments = attachments,
            isFavored = isFavored
        )
    }

    fun toRecipe(isFavored: Boolean = false): Recipe {
        return Recipe(
            id = recipeId,
            ownerId = ownerId,
            bitmapId = recipeBitmapId,
            authorLogin = authorLogin,
            name = name,
            description = description,
            cookingTime = cookingTime,
            recipeJson = recipeJson,
            likesAmount = likesAmount,
            dislikesAmount = dislikesAmount,
            viewsAmount = viewsAmount,
            preparationsAmount = preparationsAmount,
            reviewsAmount = reviewsAmount,
            products = products,
            categories = categories,
            attachmentIds = attachments.map { it.uid },
            publicationDate = publicationDate,
            isFavored = isFavored
        )
    }
}
