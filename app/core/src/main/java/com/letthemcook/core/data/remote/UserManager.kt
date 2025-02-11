package com.letthemcook.core.data.remote

import com.letthemcook.core.domain.http.RequestPair
import com.letthemcook.core.domain.http.get
import com.letthemcook.core.domain.http.post
import com.letthemcook.core.domain.http.put
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.auth.passwordChange.PasswordChangeResult
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.extensions.toBitmap
import com.letthemcook.core.domain.model.items.UserItemData
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import io.ktor.util.StringValues
import java.net.URLEncoder

class UserManager(
    private val authManager: AuthManager,
    private val remoteFileManager: RemoteFileManager
) {

    suspend fun searchUsers(
        page: Int? = null,
        perPage: Int = 10,
        searchText: String
    ): List<UserItemData> {
        if (!authManager.checkAccessTokenAndTryRefresh()) return emptyList()

        return get(
            urlString = "/users",
            params = StringValues.build {
                append("page", page.toString())
                append("perPage", perPage.toString())
                append("searchText", URLEncoder.encode(searchText, "utf-8"))
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { response ->
                val users = response.body<List<User>>()

                users.map { user ->
                    val params = RemoteFileManager.RequestParams(
                        userId = user.id,
                        fileId = user.profileBitmapId.toString(),
                        type = FileType.IMAGE
                    )
                    val recipeImage = remoteFileManager.getFile(params)

                    user.asItemData(recipeImage?.toBitmap())
                }
            },
            onError = { emptyList() }
        )
    }

    suspend fun getUser(userId: String): User? {
        if (!authManager.checkAccessTokenAndTryRefresh()) return null

        return get(
            urlString = "/user",
            params = StringValues.build {
                append("userId", userId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { it.body<User>() },
            onError = { null }
        )
    }

    suspend fun updateUser(user: User): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return put(
            urlString = "/user",
            body = user,
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun follow(userId: String) {
        if (!authManager.checkAccessTokenAndTryRefresh()) return

        post(
            urlString = "/follow",
            params = StringValues.build {
                append("userId", userId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { },
            onError = { }
        )
    }

    suspend fun unfollow(userId: String) {
        if (!authManager.checkAccessTokenAndTryRefresh()) return

        post(
            urlString = "/unfollow",
            params = StringValues.build {
                append("userId", userId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { },
            onError = { }
        )
    }

    suspend fun updatePassword(userId: String, oldPassword: String, password: String): PasswordChangeResult {
        if (!authManager.checkAccessTokenAndTryRefresh()) return PasswordChangeResult.Failed

        return put(
            urlString = "/update_password",
            body = RequestPair(oldPassword, password),
            params = StringValues.build {
                append("userId", userId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { PasswordChangeResult.Successful },
            onError = { code ->
                when (code) {
                    HttpStatusCode.Forbidden -> PasswordChangeResult.OldPasswordIsIncorrect
                    else -> PasswordChangeResult.Failed
                }
            }
        )
    }
}