package com.letthemcook.core.domain.format

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getLongTime(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Long {
    return hours * 60 * 60 * 1000L + minutes * 60 * 1000L + seconds * 1000L
}

fun Long.toShortTimeString(): String {
    val format = if (this > 3600000L) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    } else {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    return format.format(Date(this))
}