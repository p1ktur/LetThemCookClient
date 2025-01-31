package com.letthemcook.core.data.authorization

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.letthemcook.core.domain.http.post
import com.letthemcook.core.domain.model.auth.LoginAuthResult
import com.letthemcook.core.domain.model.auth.LoginData
import com.letthemcook.core.domain.model.auth.RegistrationAuthResult
import com.letthemcook.core.domain.model.auth.RegistrationData
import com.letthemcook.core.domain.model.auth.TokenResponse
import io.ktor.client.call.body

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

    private val accessTokenKey = "AccessTokenKey"
    private val refreshTokenKey = "RefreshTokenKey"

    // Tokens
    fun getAccessToken(): String? {
        return sharedPreferences.getString(accessTokenKey, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(refreshTokenKey, null)
    }

    private fun setAccessToken(token: String?) {
        sharedPreferences.edit().putString(accessTokenKey, token).apply()
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
                val tokens = response.body<TokenResponse>()
                setAccessToken(tokens.accessToken)
                setRefreshToken(tokens.refreshToken)

                RegistrationAuthResult.Successful
            },
            onError = {
                //TODO more errors
                RegistrationAuthResult.Failed
            }
        )
    }

    suspend fun login(loginData: LoginData): LoginAuthResult {
        return post(
            urlString = "/login",
            body = loginData,
            onResponse = { response ->
                val tokens = response.body<TokenResponse>()
                setAccessToken(tokens.accessToken)
                setRefreshToken(tokens.refreshToken)

                LoginAuthResult.Successful
            },
            onError = {
                //TODO more errors
                LoginAuthResult.Failed
            }
        )
    }
}