package com.letthemcook.core.domain.model.auth.tokens

interface TokenCheckResult {
    data object OK : TokenCheckResult
    data object Expired : TokenCheckResult
}