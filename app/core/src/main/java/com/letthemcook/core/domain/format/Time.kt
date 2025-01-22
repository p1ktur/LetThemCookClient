package com.letthemcook.core.domain.format

fun getLongTime(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Long {
    return hours * 60 * 60 * 1000L + minutes * 60 * 1000L + seconds * 1000L
}

fun Long.toShortTimeString(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    val secondsText = (seconds % 60).appendToTwoPlaces()
    val minutesText = (minutes % 60).appendToTwoPlaces()
    val hoursText = hours.appendToTwoPlaces()

    return if (hours > 0) {
        "$hoursText:$minutesText:$secondsText"
    } else {
        "$minutesText:$secondsText"
    }
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