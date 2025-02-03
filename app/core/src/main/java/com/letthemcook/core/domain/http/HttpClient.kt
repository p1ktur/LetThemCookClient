package com.letthemcook.core.domain.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val ClientJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = true
    prettyPrint = true
}

val HttpClient = HttpClient(Android) {
    install(DefaultRequest) {
        host = "10.0.2.2"
        port = 8080
    }
    install(ContentNegotiation) {
        json(ClientJson)
    }
    install(Logging) {
        level = LogLevel.ALL
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 150_000
    }
    install(HttpRequestRetry) {
        maxRetries = 3
        delayMillis { 5_000L * it }
    }
}