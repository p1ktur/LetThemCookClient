package com.letthemcook.recipe.domain.viewModels.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.data.remote.ReviewManager
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.extensions.toBitmap
import com.letthemcook.core.domain.model.file.extensions.toBytes
import com.letthemcook.core.domain.model.remote.Review
import com.letthemcook.core.domain.model.remote.reactions.RecipeReaction
import com.letthemcook.core.domain.model.remote.reactions.ReviewLike
import com.letthemcook.core.domain.model.status.LikeStatus
import com.letthemcook.recipe.domain.model.LoadingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class RecipeViewModel(
    private val recipeId: String,
    private val authManager: AuthManager,
    private val localDataManager: LocalDataManager,
    private val localFileManager: LocalFileManager,
    private val remoteFileManager: RemoteFileManager,
    private val recipeManager: RecipeManager,
    private val reviewManager: ReviewManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState(recipeId = recipeId))
    val uiState = _uiState.asStateFlow()

    private var lastReviewPage = 0
    private var allReviewPagesReached = false

    fun onUiAction(action: RecipeUiAction) {
        when (action) {
            RecipeUiAction.NavigateBack -> Unit
            RecipeUiAction.NavigateToHome -> Unit
            RecipeUiAction.NavigateToNewRecipe -> Unit
            RecipeUiAction.NavigateToProfile -> Unit
            is RecipeUiAction.NavigateToOtherProfile -> Unit
            RecipeUiAction.EditRecipe -> Unit

            is RecipeUiAction.ViewMediaFile -> Unit
            is RecipeUiAction.ViewRecipeBitmap -> Unit

            RecipeUiAction.LoadData -> loadData()

            RecipeUiAction.Favor -> addToFavorites()
            RecipeUiAction.RemoveFromFavored -> removeFromFavorites()

            is RecipeUiAction.Cook -> prepareRecipe()

            RecipeUiAction.LikeRecipe -> likeRecipe()
            RecipeUiAction.UnlikeRecipe -> unlikeRecipe()
            RecipeUiAction.DislikeRecipe -> dislikeRecipe()
            RecipeUiAction.UnDislikeRecipe -> unDislikeRecipe()

            RecipeUiAction.LoadReviews -> loadReviews()
            is RecipeUiAction.SendReview -> sendReview(action.text)
            is RecipeUiAction.LikeReview -> likeReview(action.index)
            is RecipeUiAction.UnlikeReview -> unlikeReview(action.index)
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = authManager.getUser()?.id
            val recipe = recipeManager.getRecipe(recipeId)

            if (userId != null && recipe?.ownerId == userId) {
                _uiState.update {
                    it.copy(
                        isOwner = true
                    )
                }
            } else {
                recipeManager.viewRecipe(recipeId)
            }

            if (recipe == null) {
                if (uiState.value.loadingStatus == LoadingStatus.SUCCESS) return@launch

                _uiState.update {
                    it.copy(
                        loadingStatus = LoadingStatus.FAILED
                    )
                }
                return@launch
            }

            val bitmap = recipe.getBitmap(remoteFileManager)

            val isLiked = when (recipe.getLikeStatus(localDataManager)) {
                null -> LikeStatus.NONE
                true -> LikeStatus.LIKED
                false -> LikeStatus.DISLIKED
            }

            val attachments = recipe.getAttachments(localFileManager, remoteFileManager)

            val isFavored = localDataManager.getRecipe(recipe.id)?.isFavored == true

            _uiState.update {
                it.fromRecipe(
                    recipe = recipe,
                    bitmap = bitmap,
                    isLiked = isLiked,
                    attachments = attachments,
                    isFavored = isFavored
                )
            }

            _uiState.update {
                it.copy(
                    loadingStatus = LoadingStatus.SUCCESS
                )
            }

            loadReviews()
        }
    }

    private fun addToFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isFavored = true
                )
            }

            localDataManager.saveRecipe(uiState.value.toRecipe(true))
            uiState.value.recipeBitmap?.let { bitmap ->
                localFileManager.saveFile(bitmap.toBytes(), FileType.IMAGE, uiState.value.recipeBitmapId.toString())
            }
        }
    }

    private fun removeFromFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isFavored = false
                )
            }

            if (!uiState.value.isOwner) {
                localDataManager.deleteRecipeById(uiState.value.recipeId)
                uiState.value.recipeBitmapId?.let { fileId ->
                    localFileManager.getFileByUid(fileId)?.let { file ->
                        localFileManager.deleteFile(file)
                    }
                }
            }
        }
    }

    // Can be changed so that it prepares recipes only if any steps were done during cooking
    // or cooking was finished so it cannot be abused by simple entering cooking and leaving it
    private fun prepareRecipe() {
        if (uiState.value.isOwner) return

        viewModelScope.launch(Dispatchers.IO) {
            recipeManager.prepareRecipe(recipeId)
        }
    }

    private fun likeRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            _uiState.update {
                it.copy(
                    likeStatus = LikeStatus.LIKED
                )
            }

            val prevRecipeReaction = localDataManager.getRecipeReaction(uiState.value.recipeId)

            val recipeReaction = RecipeReaction(
                id = prevRecipeReaction?.id ?: 0,
                recipeId = uiState.value.recipeId,
                ownerId = uiState.value.ownerId,
                liked = true
            )
            localDataManager.saveRecipeReaction(recipeReaction)

            val recipe = uiState.value.toRecipe()
            if (prevLikeStatus == false) recipe.dislikesAmount--
            recipe.likesAmount++

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, true)) {
                if (uiState.value.isOwner || uiState.value.isFavored) localDataManager.saveRecipe(recipe)
                _uiState.update {
                    it.copy(
                        dislikesAmount = recipe.dislikesAmount,
                        likesAmount = recipe.likesAmount
                    )
                }
            }
        }
    }

    private fun unlikeRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            _uiState.update {
                it.copy(
                    likeStatus = LikeStatus.NONE
                )
            }

            localDataManager.getRecipeReaction(uiState.value.recipeId)?.let { recipeReaction ->
                localDataManager.deleteRecipeReaction(recipeReaction)
            }

            val recipe = uiState.value.toRecipe()
            recipe.likesAmount--

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, null)) {
                if (uiState.value.isOwner || uiState.value.isFavored) localDataManager.saveRecipe(recipe)
                _uiState.update {
                    it.copy(
                        likeStatus = LikeStatus.NONE,
                        likesAmount = recipe.likesAmount
                    )
                }
            }
        }
    }

    private fun dislikeRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            _uiState.update {
                it.copy(
                    likeStatus = LikeStatus.DISLIKED
                )
            }

            val prevRecipeReaction = localDataManager.getRecipeReaction(uiState.value.recipeId)

            val recipeReaction = RecipeReaction(
                id = prevRecipeReaction?.id ?: 0,
                recipeId = uiState.value.recipeId,
                ownerId = uiState.value.ownerId,
                liked = false
            )
            localDataManager.saveRecipeReaction(recipeReaction)

            val recipe = uiState.value.toRecipe()
            if (prevLikeStatus == true) recipe.likesAmount--
            recipe.dislikesAmount++

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, false)) {
                if (uiState.value.isOwner || uiState.value.isFavored) localDataManager.saveRecipe(recipe)
                _uiState.update {
                    it.copy(
                        likeStatus = LikeStatus.DISLIKED,
                        dislikesAmount = recipe.dislikesAmount,
                        likesAmount = recipe.likesAmount
                    )
                }
            }
        }
    }

    private fun unDislikeRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            _uiState.update {
                it.copy(
                    likeStatus = LikeStatus.NONE
                )
            }

            localDataManager.getRecipeReaction(uiState.value.recipeId)?.let { recipeReaction ->
                localDataManager.deleteRecipeReaction(recipeReaction)
            }

            val recipe = uiState.value.toRecipe()
            recipe.dislikesAmount--

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, null)) {
                if (uiState.value.isOwner || uiState.value.isFavored) localDataManager.saveRecipe(recipe)
                _uiState.update {
                    it.copy(
                        dislikesAmount = recipe.dislikesAmount
                    )
                }
            }
        }
    }

    private fun loadReviews() {
        _uiState.update {
            it.copy(
                loadingReviews = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val perPage = 20
            val reviews = reviewManager.getReviews(
                page = lastReviewPage++,
                perPage = perPage,
                recipeId = uiState.value.recipeId
            )

            if (reviews.size < perPage) allReviewPagesReached = true

            _uiState.update {
                it.copy(
                    reviews = (it.reviews + reviews).distinctBy { review -> review.id },
                    loadingReviews = false
                )
            }
        }
    }

    private fun sendReview(text: String) {
        val user = authManager.getUser() ?: return
        if (text.isEmpty()) return

        val review = Review(
            id = UUID.randomUUID().toString(),
            authorId = user.id,
            recipeId = uiState.value.recipeId,
            reviewText = text,
            likesAmount = 0
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (reviewManager.sendReview(review)) {
                val userProfileBitmap = user.profileBitmapId?.let {
                    localFileManager.getFileByUid(it)?.let { file ->
                        localFileManager.getFileBytes(file)?.toBitmap()
                    }
                }
                val reviewItemData = review.asItemData(user.login, userProfileBitmap, false)

                _uiState.update {
                    it.copy(
                        reviewsAmount = it.reviewsAmount + 1,
                        reviews = listOf(reviewItemData) + it.reviews
                    )
                }
            }
        }
    }

    private fun likeReview(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val review = uiState.value.reviews[index]
                val reviewLike = ReviewLike(
                    reviewId = review.id,
                    ownerId = uiState.value.ownerId
                )

                localDataManager.saveReviewLike(reviewLike)

                review.likesAmount++

                reviewManager.likeReview(review.id)
            } catch (_: Exception) {}
        }
    }

    private fun unlikeReview(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val review = uiState.value.reviews[index]
                localDataManager.getReviewLike(review.id)?.let { reviewLike ->
                    localDataManager.deleteReviewLike(reviewLike)
                }

                review.likesAmount--

                reviewManager.unlikeReview(review.id)
            } catch (_: Exception) {}
        }
    }
}