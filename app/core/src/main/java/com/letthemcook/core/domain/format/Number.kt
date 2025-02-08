package com.letthemcook.core.domain.format

import java.util.Locale

fun Float.cute() = "%.2f".format(this)

fun Int.cute(): String {
    return when {
        this < 1000 -> this.toString()
        this < 10_000 -> String.format(Locale.getDefault(),"%.1fК", this / 1000.0)
        this < 1_000_000 -> (this / 1000).toString() + "К"
        this < 10_000_000 -> String.format(Locale.getDefault(), "%.1fМ", this / 1_000_000.0)
        else -> (this / 1_000_000).toString() + "М"
    }
}