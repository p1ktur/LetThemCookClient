package com.letthemcook.core.data.remote

import com.letthemcook.core.domain.http.delete
import com.letthemcook.core.domain.http.get
import com.letthemcook.core.domain.http.postFile
import com.letthemcook.core.domain.model.file.FileType
import io.ktor.client.call.body
import io.ktor.util.StringValues
import io.ktor.util.StringValuesBuilder
import kotlinx.serialization.Serializable

class RemoteFileManager(
    private val authManager: AuthManager
) {

    @Serializable
    data class RequestParams(
        val userId: String,
        val fileId: String,
        val recipeId: String? = null,
        val blockId: String? = null,
        val type: FileType,
        val isAttachment: Boolean = false
    ) {
        fun appendToParams(builder: StringValuesBuilder) {
            builder.append("userId", userId)
            builder.append("fileId", fileId)
            builder.append("fileType", when (type) {
                FileType.IMAGE -> "image"
                FileType.VIDEO -> "video"
                FileType.ANY -> "any"
            })
            recipeId?.let { builder.append("recipeId", it) }
            blockId?.let { builder.append("blockId", it) }
            builder.append("isAttachment", isAttachment.toString())
        }
    }

    suspend fun getFile(params: RequestParams): ByteArray? {
        if (!authManager.checkAccessTokenAndTryRefresh()) return null

        return get(
            urlString = "/file",
            params = StringValues.build {
                params.appendToParams(this)
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

        return postFile(
            urlString = "/file",
            bytes = bytes,
            params = StringValues.build {
                params.appendToParams(this)
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
                params.appendToParams(this)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }
}