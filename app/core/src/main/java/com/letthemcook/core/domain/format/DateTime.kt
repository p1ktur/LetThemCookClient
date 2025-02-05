package com.letthemcook.core.domain.format

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.prettyString(): String {
    return format(DateTimeFormatter.ISO_DATE_TIME)
}