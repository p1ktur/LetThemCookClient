package com.letthemcook.core.domain.format

import android.content.Context
import com.letthemcook.core.R

fun getLongTime(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Long {
    return hours * 60 * 60 * 1000L + minutes * 60 * 1000L + seconds * 1000L
}

fun Long.isLongTime(): Boolean {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return hours > 0
}

fun Long.toShortTimeString(context: Context, canInstant: Boolean = true): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    if (this == 0L && canInstant) return context.getString(R.string.instant)

    val secondsText = (seconds % 60).appendToTwoPlaces()
    val minutesText = (minutes % 60).appendToTwoPlaces()
    val hoursText = hours.appendToTwoPlaces()

    return when {
        hours > 0 -> "$hoursText:$minutesText:$secondsText"
        else -> "$minutesText:$secondsText"
    }
}

fun Long.toTimeString(context: Context, canInstant: Boolean = true): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    if (this == 0L && canInstant) return context.getString(R.string.instant)

    val secondsText = (seconds % 60).appendToTwoPlaces()
    val minutesText = (minutes % 60).appendToTwoPlaces()
    val hoursText = hours.appendToTwoPlaces()

    return "$hoursText:$minutesText:$secondsText"
}

fun Long.toSecondsString(): String {
    val seconds = this / 1000

    val secondsText = (seconds % 60).appendToTwoPlaces()

    return secondsText
}

fun Long.toMinutesString(): String {
    val seconds = this / 1000
    val minutes = seconds / 60

    val minutesText = (minutes % 60).appendToTwoPlaces()

    return minutesText
}

fun Long.toHoursString(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    val hoursText = hours.appendToTwoPlaces()

    return hoursText
}

fun Long.appendToTwoPlaces(): String {
    return if (this < 10) "0$this" else this.toString()
}