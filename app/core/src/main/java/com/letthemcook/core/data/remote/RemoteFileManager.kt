package com.letthemcook.core.data.remote

import com.letthemcook.core.domain.http.delete
import com.letthemcook.core.domain.http.get
import com.letthemcook.core.domain.http.postFile
import com.letthemcook.core.domain.model.file.FileType
import io.ktor.client.call.body
import io.ktor.util.StringValues

class RemoteFileManager(
    private val authManager: AuthManager
) {

    data class RequestParams(
        val userId: String,
        val fileId: String,
        val recipeId: String? = null,
        val blockId: String? = null,
        val type: FileType,
        val isAttachment: Boolean = false
    )

    suspend fun getFile(params: RequestParams): ByteArray? {
        if (!authManager.checkAccessTokenAndTryRefresh()) return null

        return get(
            urlString = "/file",
            params = StringValues.build {
                append("userId", params.userId)
                append("fileId", params.fileId)
                append("fileType", when (params.type) {
                    FileType.IMAGE -> "image"
                    FileType.VIDEO -> "video"
                    FileType.ANY -> "any"
                })
                params.recipeId?.let { append("recipeId", it) }
                params.blockId?.let { append("blockId", it) }
                append("blockId", params.isAttachment.toString())
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { it.body<ByteArray>() },
            onError = { null }
        )
    }

    suspend fun uploadFile(params: RequestParams, bytes: ByteArray): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        // TODO if video -> compress it

        return postFile(
            urlString = "/file",
            bytes = bytes,
            params = StringValues.build {
                append("userId", params.userId)
                append("fileId", params.fileId)
                append("fileType", when (params.type) {
                    FileType.IMAGE -> "image"
                    FileType.VIDEO -> "video"
                    FileType.ANY -> "any"
                })
                params.recipeId?.let { append("recipeId", it) }
                params.blockId?.let { append("blockId", it) }
                append("blockId", params.isAttachment.toString())
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun deleteFile(params: RequestParams): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return delete(
            urlString = "/file",
            params = StringValues.build {
                append("userId", params.userId)
                append("fileId", params.fileId)
                append("fileType", when (params.type) {
                    FileType.IMAGE -> "image"
                    FileType.VIDEO -> "video"
                    FileType.ANY -> "any"
                })
                params.recipeId?.let { append("recipeId", it) }
                params.blockId?.let { append("blockId", it) }
                append("blockId", params.isAttachment.toString())
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }
}