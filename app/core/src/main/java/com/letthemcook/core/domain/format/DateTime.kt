package com.letthemcook.core.domain.format

import java.time.LocalDateTime

fun LocalDateTime.prettyString(): String {
    val dayString = if (dayOfMonth in 0..9) "0$dayOfMonth" else dayOfMonth
    val monthString = if (monthValue in 0..9) "0$monthValue" else monthValue
    return "$dayString.$monthString.$year"
}