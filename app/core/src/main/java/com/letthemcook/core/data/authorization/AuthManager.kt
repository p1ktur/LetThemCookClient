package com.letthemcook.core.data.authorization

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.letthemcook.core.domain.http.ClientJson
import com.letthemcook.core.domain.http.HttpResult
import com.letthemcook.core.domain.http.get
import com.letthemcook.core.domain.http.post
import com.letthemcook.core.domain.model.auth.login.LoginAuthResult
import com.letthemcook.core.domain.model.auth.login.LoginData
import com.letthemcook.core.domain.model.auth.registration.RegistrationAuthResult
import com.letthemcook.core.domain.model.auth.registration.RegistrationData
import com.letthemcook.core.domain.model.auth.tokens.TokenResponse
import com.letthemcook.core.domain.model.auth.User
import com.letthemcook.core.domain.model.auth.tokens.TokenCheckResult
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import io.ktor.util.StringValues

class AuthManager(context: Context) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "AuthorizationStorage",
        MasterKey.Builder(context, "AuthorizationMasterKey").run {
            setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            build()
        },
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val userKey = "UserKey"
    private val accessTokenKey = "AccessTokenKey"
    private val refreshTokenKey = "RefreshTokenKey"

    // User
    fun getUser(): User? {
        val jsonString = sharedPreferences.getString(userKey, null) ?: return null

        return ClientJson.decodeFromString(jsonString)
    }

    fun setUser(user: User) {
        val jsonString = ClientJson.encodeToString(user)

        sharedPreferences.edit().putString(userKey, jsonString).apply()
    }

    // Tokens
    fun getAccessToken(): String? {
        return sharedPreferences.getString(accessTokenKey, null)
    }

    private fun setAccessToken(token: String?) {
        sharedPreferences.edit().putString(accessTokenKey, token).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(refreshTokenKey, null)
    }

    private fun setRefreshToken(token: String?) {
        sharedPreferences.edit().putString(refreshTokenKey, token).apply()
    }

    //Authorization
    suspend fun register(registrationData: RegistrationData): RegistrationAuthResult {
        return post(
            urlString = "/register",
            body = registrationData,
            onResponse = { response ->
                val response = response.body<TokenResponse>()

                setUser(response.user)
                setAccessToken(response.accessToken)
                setRefreshToken(response.refreshToken)

                RegistrationAuthResult.Successful
            },
            onError = { code ->
                when (code) {
                    HttpStatusCode.Conflict -> RegistrationAuthResult.UserAlreadyExists
                    else -> RegistrationAuthResult.Failed
                }
            }
        )
    }

    suspend fun login(loginData: LoginData): LoginAuthResult {
        return post(
            urlString = "/login",
            body = loginData,
            onResponse = { response ->
                val response = response.body<TokenResponse>()

                setUser(response.user)
                setAccessToken(response.accessToken)
                setRefreshToken(response.refreshToken)

                LoginAuthResult.Successful
            },
            onError = { code ->
                when (code) {
                    HttpStatusCode.Conflict -> LoginAuthResult.UserDoesNotExist
                    HttpStatusCode.BadRequest -> LoginAuthResult.WrongPassword
                    else -> LoginAuthResult.Failed
                }
            }
        )
    }

    //Tokens
    suspend fun checkAccessToken(): TokenCheckResult {
        return get(
            urlString = "/check_at",
            headers = StringValues.build {
                append("Authorization", "Bearer ${getAccessToken()}")
            },
            onResponse = { TokenCheckResult.OK },
            onError = { TokenCheckResult.Expired }
        )
    }

    suspend fun checkRefreshToken(): TokenCheckResult {
        return get(
            urlString = "/check_rt",
            headers = StringValues.build {
                append("Refresh Token", "Bearer ${getRefreshToken()}")
            },
            onResponse = { TokenCheckResult.OK },
            onError = { TokenCheckResult.Expired }
        )
    }

    suspend fun refreshTokens(): HttpResult {
        return get(
            urlString = "/refresh_tokens",
            params = StringValues.build {
                getUser()?.id?.let { id -> append("userId", id) }
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${getAccessToken()}")
                append("Refresh Token", "Bearer ${getRefreshToken()}")
            },
            onResponse = { response ->
                val response = response.body<TokenResponse>()

                setAccessToken(response.accessToken)
                setRefreshToken(response.refreshToken)

                HttpResult.Success
            },
            onError = {
                HttpResult.Failure
            }
        )
    }
}