package com.letthemcook.core.domain.http

sealed interface HttpResult {
    data object Success : HttpResult
    data object Failure : HttpResult
}