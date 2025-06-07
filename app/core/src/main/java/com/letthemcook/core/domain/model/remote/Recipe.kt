package com.letthemcook.core.domain.model.remote

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.domain.dataConvertion.serialization.LocalDateTimeSerializer
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.extensions.toBitmap
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.core.domain.model.status.LikeStatus
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Serializable
data class Recipe(
    @PrimaryKey(autoGenerate = false) var id: String = UUID.randomUUID().toString(),
    var ownerId: String = UUID.randomUUID().toString(),
    var bitmapId: String? = null,
    var authorLogin: String = "",
    var name: String? = null,
    var description: String? = null,
    var cookingTime: Long? = null,
    var recipeJson: String? = null,
    var likesAmount: Int = 0,
    var dislikesAmount: Int = 0,
    var viewsAmount: Int = 0,
    var preparationsAmount: Int = 0,
    var reviewsAmount: Int = 0,
    var products: List<WeightedProduct> = emptyList(),
    var categories: List<Category> = emptyList(),
    var attachmentIds: List<String> = emptyList(),
    @Serializable(with = LocalDateTimeSerializer::class) var publicationDate: LocalDateTime? = null,
    var isFavored: Boolean = false
) {
    fun asItemData(bitmap: Bitmap?, likeStatus: LikeStatus): RecipeItemData {
        return RecipeItemData(
            id = id,
            name = name ?: "",
            ownerId = ownerId,
            authorLogin = authorLogin,
            likesAmount = likesAmount,
            dislikesAmount = dislikesAmount,
            reviewsAmount = reviewsAmount,
            preparationsAmount = preparationsAmount,
            viewsAmount = viewsAmount,
            description = description ?: "",
            likeStatus = likeStatus,
            bitmap = bitmap,
            publicationDate = publicationDate
        )
    }

    suspend fun updateUserInteractionsData(recipeManager: RecipeManager, language: String) {
        recipeManager.getRecipe(id, language)?.let { remoteRecipe ->
            likesAmount = remoteRecipe.likesAmount
            dislikesAmount = remoteRecipe.dislikesAmount
            preparationsAmount = remoteRecipe.preparationsAmount
            reviewsAmount = remoteRecipe.reviewsAmount
            viewsAmount = remoteRecipe.viewsAmount
        }
    }

    suspend fun getBitmap(localFileManager: LocalFileManager, remoteFileManager: RemoteFileManager): Bitmap? {
        val bitmapId = bitmapId

        if (bitmapId != null) {
            val localFile = localFileManager.getFileByUid(bitmapId)

            if (localFile != null) {
                return localFileManager.getFileBytes(localFile)?.toBitmap()
            } else {
                val params = RemoteFileManager.RequestParams(
                    userId = ownerId,
                    fileId = bitmapId,
                    recipeId = id,
                    type = FileType.IMAGE
                )

                return remoteFileManager.getFile(params)?.toBitmap()
            }
        }

        return null
    }

    suspend fun getBitmap(remoteFileManager: RemoteFileManager): Bitmap? {
        val bitmapId = bitmapId

        if (bitmapId != null) {
            val params = RemoteFileManager.RequestParams(
                userId = ownerId,
                fileId = bitmapId,
                recipeId = id,
                type = FileType.IMAGE
            )

            return remoteFileManager.getFile(params)?.toBitmap()
        }

        return null
    }

    suspend fun getLikeStatus(localDataManager: LocalDataManager): Boolean? {
        return localDataManager.getRecipeReaction(id)?.liked
    }

    suspend fun getAttachmentsAndSave(localFileManager: LocalFileManager, remoteFileManager: RemoteFileManager): List<File> {
        val attachmentFiles = mutableListOf<File>()

        attachmentIds.forEach { fileId ->
            val localFile = localFileManager.getFileByUid(fileId)

            if (localFile != null) {
                attachmentFiles.add(localFile)
            } else {
                var remoteFileBytes: ByteArray?
                var remoteFileType = FileType.IMAGE

                val paramsForImage = RemoteFileManager.RequestParams(
                    userId = ownerId,
                    fileId = fileId,
                    recipeId = id,
                    type = remoteFileType,
                    isAttachment = true
                )

                remoteFileBytes = remoteFileManager.getFile(paramsForImage)

                if (remoteFileBytes == null) {
                    remoteFileType = FileType.VIDEO
                    val paramsForVideo = paramsForImage.copy(type = remoteFileType)
                    remoteFileBytes = remoteFileManager.getFile(paramsForVideo)
                }

                if (remoteFileBytes != null) {
                    localFileManager.saveFile(remoteFileBytes, remoteFileType, fileId)?.let { savedFile ->
                        attachmentFiles.add(savedFile)
                    }
                }
            }
        }

        return attachmentFiles
    }

    suspend fun getAttachments(localFileManager: LocalFileManager, remoteFileManager: RemoteFileManager): List<File> {
        val attachmentFiles = mutableListOf<File>()

        attachmentIds.forEach { fileId ->
            var remoteFileBytes: ByteArray?
            var remoteFileType = FileType.IMAGE

            val paramsForImage = RemoteFileManager.RequestParams(
                userId = ownerId,
                fileId = fileId,
                recipeId = id,
                type = remoteFileType,
                isAttachment = true
            )

            remoteFileBytes = remoteFileManager.getFile(paramsForImage)

            if (remoteFileBytes == null) {
                remoteFileType = FileType.VIDEO
                val paramsForVideo = paramsForImage.copy(type = remoteFileType)
                remoteFileBytes = remoteFileManager.getFile(paramsForVideo)
            }

            if (remoteFileBytes != null) {
                val file = localFileManager.createTemporaryFile(fileId, remoteFileType, remoteFileBytes)

                attachmentFiles.add(file)
            }
        }

        return attachmentFiles
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (ownerId != other.ownerId) return false
        if (bitmapId != other.bitmapId) return false
        if (authorLogin != other.authorLogin) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (cookingTime != other.cookingTime) return false
        if (recipeJson != other.recipeJson) return false
        if (likesAmount != other.likesAmount) return false
        if (dislikesAmount != other.dislikesAmount) return false
        if (viewsAmount != other.viewsAmount) return false
        if (preparationsAmount != other.preparationsAmount) return false
        if (reviewsAmount != other.reviewsAmount) return false
        if (products != other.products) return false
        if (categories != other.categories) return false
        if (attachmentIds != other.attachmentIds) return false
        if (publicationDate != other.publicationDate) return false
        if (isFavored != other.isFavored) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + (bitmapId?.hashCode() ?: 0)
        result = 31 * result + authorLogin.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (cookingTime?.hashCode() ?: 0)
        result = 31 * result + (recipeJson?.hashCode() ?: 0)
        result = 31 * result + likesAmount
        result = 31 * result + dislikesAmount
        result = 31 * result + viewsAmount
        result = 31 * result + preparationsAmount
        result = 31 * result + reviewsAmount
        result = 31 * result + products.hashCode()
        result = 31 * result + categories.hashCode()
        result = 31 * result + attachmentIds.hashCode()
        result = 31 * result + (publicationDate?.hashCode() ?: 0)
        return result
    }
}
