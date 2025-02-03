package com.letthemcook.core.data.remote.user

import android.util.Log
import com.letthemcook.core.data.remote.authorization.AuthManager
import com.letthemcook.core.domain.http.RequestPair
import com.letthemcook.core.domain.http.get
import com.letthemcook.core.domain.http.put
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.auth.passwordChange.PasswordChangeResult
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import io.ktor.util.StringValues

class UserManager(
    private val authManager: AuthManager
) {

    suspend fun getUser(userId: String): User? {
        if (!authManager.checkAccessTokenAndTryRefresh()) return null

        return get(
            urlString = "/user",
            params = StringValues.build {
                append("id", userId)
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

    suspend fun updatePassword(userId: String, oldPassword: String, password: String): PasswordChangeResult {
        if (!authManager.checkAccessTokenAndTryRefresh()) return PasswordChangeResult.Failed

        return put(
            urlString = "/update_password",
            body = RequestPair(oldPassword, password),
            params = StringValues.build {
                append("id", userId)
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