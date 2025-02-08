package com.letthemcook.core.data.remote

import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.domain.http.get
import com.letthemcook.core.domain.http.post
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.items.ReviewItemData
import com.letthemcook.core.domain.model.remote.Review
import io.ktor.client.call.body
import io.ktor.util.StringValues

class ReviewManager(
    private val authManager: AuthManager,
    private val userManager: UserManager,
    private val localDataManager: LocalDataManager,
    private val remoteFileManager: RemoteFileManager
) {

    suspend fun getReviews(recipeId: String, page: Int? = null, perPage: Int = 10): List<ReviewItemData> {
        if (!authManager.checkAccessTokenAndTryRefresh()) return emptyList()

        return get(
            urlString = "/reviews",
            params = StringValues.build {
                append("page", page.toString())
                append("perPage", perPage.toString())
                append("recipeId", recipeId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { response ->
                val reviews = response.body<List<Review>>()

                reviews.map { review ->
                    val user = userManager.getUser(review.authorId)

                    val params = RemoteFileManager.RequestParams(
                        userId = review.authorId,
                        fileId = user?.profileBitmapId.toString(),
                        type = FileType.IMAGE
                    )
                    val profileImage = remoteFileManager.getFile(params)

                    val isLiked = localDataManager.getReviewLike(review.id) != null

                    review.asItemData(user?.login.toString(), profileImage?.toBitmap(), isLiked)
                }
            },
            onError = { emptyList() }
        )
    }

    suspend fun sendReview(review: Review): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return post(
            urlString = "/review",
            body = review,
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun likeReview(reviewId: String): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return post(
            urlString = "/review_like",
            params = StringValues.build {
                append("reviewId", reviewId)
                append("liked", true.toString())
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun unlikeReview(reviewId: String): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return post(
            urlString = "/review_like",
            params = StringValues.build {
                append("reviewId", reviewId)
                append("liked", false.toString())
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }
}