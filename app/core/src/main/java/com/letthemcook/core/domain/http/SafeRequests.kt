package com.letthemcook.core.domain.http

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.util.StringValues

suspend inline fun <R> get(
    urlString: String,
    params: StringValues? = null,
    headers: StringValues? = null,
    onResponse: (HttpResponse) -> R,
    onError: (HttpStatusCode) -> R
): R {
    return try {
        val response = HttpClient.get(urlString) {
            contentType(ContentType.Application.Json)

            url {
                params?.let { parameters.appendAll(it) }
            }

            headers {
                headers?.let { appendAll(it) }
            }
        }

        if (response.status == HttpStatusCode.OK) {
            onResponse(response)
        } else{
            onError(response.status)
        }
    } catch (e: Exception) {
        onError(HttpStatusCode(700, e.message.toString()))
    }
}

suspend inline fun <R> post(
    urlString: String,
    body: Any,
    params: StringValues? = null,
    headers: StringValues? = null,
    onResponse: (HttpResponse) -> R,
    onError: (HttpStatusCode) -> R
): R {
    return try {
        val response = HttpClient.post(urlString) {
            contentType(ContentType.Application.Json)
            setBody(body)

            url {
                params?.let { parameters.appendAll(it) }
            }

            headers {
                headers?.let { appendAll(it) }
            }
        }

        if (response.status == HttpStatusCode.OK) {
            onResponse(response)
        } else{
            onError(response.status)
        }
    } catch (e: Exception) {
        onError(HttpStatusCode(700, e.message.toString()))
    }
}

suspend inline fun <R> put(
    urlString: String,
    body: Any,
    params: StringValues? = null,
    headers: StringValues? = null,
    onResponse: (HttpResponse) -> R,
    onError: (HttpStatusCode) -> R
): R {
    return try {
        val response = HttpClient.put(urlString) {
            contentType(ContentType.Application.Json)
            setBody(body)

            url {
                params?.let { parameters.appendAll(it) }
            }

            headers {
                headers?.let { appendAll(it) }
            }
        }

        if (response.status == HttpStatusCode.OK) {
            onResponse(response)
        } else{
            onError(response.status)
        }
    } catch (e: Exception) {
        onError(HttpStatusCode(700, e.message.toString()))
    }
}

suspend inline fun <R> delete(
    urlString: String,
    params: StringValues? = null,
    headers: StringValues? = null,
    onResponse: (HttpResponse) -> R,
    onError: (HttpStatusCode) -> R
): R {
    return try {
        val response = HttpClient.delete(urlString) {
            contentType(ContentType.Application.Json)

            url {
                params?.let { parameters.appendAll(it) }
            }

            headers {
                headers?.let { appendAll(it) }
            }
        }

        if (response.status == HttpStatusCode.OK) {
            onResponse(response)
        } else{
            onError(response.status)
        }
    } catch (e: Exception) {
        onError(HttpStatusCode(700, e.message.toString()))
    }
}