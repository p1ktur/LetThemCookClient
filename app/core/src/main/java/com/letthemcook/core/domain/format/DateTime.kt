package com.letthemcook.core.domain.format

import java.time.LocalDateTime

fun LocalDateTime.prettyString(): String {
    return "$dayOfMonth.$monthValue.$year"
}