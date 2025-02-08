package com.letthemcook.recipe.domain.viewModels.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.data.remote.ReviewManager
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.items.ReviewItemData
import com.letthemcook.core.domain.model.remote.Review
import com.letthemcook.core.domain.model.remote.reactions.RecipeReaction
import com.letthemcook.core.domain.model.remote.reactions.ReviewLike
import com.letthemcook.recipe.domain.model.LikeStatus
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
            RecipeUiAction.SendReview -> sendReview()
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

            _uiState.update {
                it.fromRecipe(
                    recipe = recipe,
                    bitmap = bitmap,
                    isLiked = isLiked,
                    attachments = attachments
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
            localDataManager.saveRecipe(uiState.value.toRecipe())

            _uiState.update {
                it.copy(
                    isFavored = true
                )
            }
        }
    }

    private fun removeFromFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            localDataManager.deleteRecipeById(uiState.value.recipeId)

            _uiState.update {
                it.copy(
                    isFavored = false
                )
            }
        }
    }

    private fun prepareRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            recipeManager.prepareRecipe(recipeId)
        }
    }

    private fun likeRecipe() {
        _uiState.update {
            it.copy(
                likeStatus = LikeStatus.LIKED
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            val prevRecipeReaction = localDataManager.getRecipeReaction(uiState.value.recipeId)

            val recipeReaction = RecipeReaction(
                id = prevRecipeReaction?.id ?: 0,
                recipeId = uiState.value.recipeId,
                liked = true
            )
            localDataManager.saveRecipeReaction(recipeReaction)

            val recipe = uiState.value.toRecipe()
            if (prevLikeStatus == false) recipe.dislikesAmount--
            recipe.likesAmount++

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, true)) {
                localDataManager.saveRecipe(recipe)
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
        _uiState.update {
            it.copy(
                likeStatus = LikeStatus.NONE
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            localDataManager.getRecipeReaction(uiState.value.recipeId)?.let { recipeReaction ->
                localDataManager.deleteRecipeReaction(recipeReaction)
            }

            val recipe = uiState.value.toRecipe()
            recipe.likesAmount--

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, null)) {
                localDataManager.saveRecipe(recipe)
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
        _uiState.update {
            it.copy(
                likeStatus = LikeStatus.DISLIKED
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            val prevRecipeReaction = localDataManager.getRecipeReaction(uiState.value.recipeId)

            val recipeReaction = RecipeReaction(
                id = prevRecipeReaction?.id ?: 0,
                recipeId = uiState.value.recipeId,
                liked = false
            )
            localDataManager.saveRecipeReaction(recipeReaction)

            val recipe = uiState.value.toRecipe()
            if (prevLikeStatus == true) recipe.likesAmount--
            recipe.dislikesAmount++

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, false)) {
                localDataManager.saveRecipe(recipe)
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
        _uiState.update {
            it.copy(
                likeStatus = LikeStatus.NONE
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val prevLikeStatus = uiState.value.likeStatus.toBoolean()
            localDataManager.getRecipeReaction(uiState.value.recipeId)?.let { recipeReaction ->
                localDataManager.deleteRecipeReaction(recipeReaction)
            }

            val recipe = uiState.value.toRecipe()
            recipe.dislikesAmount--

            if (recipeManager.reactOnRecipe(uiState.value.recipeId, prevLikeStatus, null)) {
                localDataManager.saveRecipe(recipe)
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
                    reviews = it.reviews + reviews,
                    loadingReviews = false
                )
            }
        }
    }

    private fun sendReview() {
        val user = authManager.getUser() ?: return
        if (uiState.value.reviewText.text.isEmpty()) return

        val review = Review(
            id = UUID.randomUUID().toString(),
            authorId = user.id,
            recipeId = uiState.value.recipeId,
            reviewText = uiState.value.reviewText.text.toString(),
            likesAmount = 0
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (reviewManager.sendReview(review)) {
                val userProfileBitmap = user.profileBitmapId?.let {
                    localFileManager.getFileByUid(it)?.let { file ->
                        localFileManager.getFileBytes(file)?.toBitmap()
                    }
                }
                val reviewItemData = ReviewItemData(
                    id = UUID.randomUUID().toString(),
                    authorId = user.id,
                    authorLogin = user.login,
                    authorBitmap = userProfileBitmap,
                    text = uiState.value.reviewText.text.toString(),
                    likesAmount = 0,
                    isLiked = false
                )

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
                val reviewLike = ReviewLike(reviewId = review.id)

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