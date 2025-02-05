package com.letthemcook.core.domain.model.recipe

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.domain.dataConvertion.serialization.LocalDateTimeSerializer
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.items.RecipeItemData
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
    fun asItemData(bitmap: Bitmap?): RecipeItemData {
        return RecipeItemData(
            id = id,
            name = name ?: "",
            authorLogin = authorLogin,
            likesAmount = likesAmount,
            dislikesAmount = dislikesAmount,
            reviewsAmount = reviewsAmount,
            preparationsAmount = preparationsAmount,
            description = description ?: "",
            bitmap = bitmap,
            publicationDate = publicationDate
        )
    }

    suspend fun updateUserInteractionsData(recipeManager: RecipeManager) {
        recipeManager.getRecipe(id)?.let { remoteRecipe ->
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
            localFileManager.getFileByUid(bitmapId)?.let { file ->
                val params = RemoteFileManager.RequestParams(
                    userId = ownerId,
                    fileId = file.uid,
                    recipeId = id,
                    type = FileType.IMAGE
                )

                return remoteFileManager.getFile(params)?.toBitmap()
            }
        }

        return null
    }

    suspend fun getLikeStatus(localDataManager: LocalDataManager): Boolean? {
        return localDataManager.getRecipeReaction(id)?.liked
    }

    suspend fun getAttachments(localFileManager: LocalFileManager, remoteFileManager: RemoteFileManager): List<File> {
        val attachmentFiles = mutableListOf<File>()

        attachmentIds.forEach { fileId ->
            val localFile = localFileManager.getFileByUid(fileId)

            if (localFile != null) {
                attachmentFiles.add(localFile)
            } else {
                var remoteFileBytes: ByteArray? = null
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
}
