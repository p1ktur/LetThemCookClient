package com.letthemcook.editor.domain.editor.geometry

import kotlin.math.*

fun Int.limit(bound1: Int, bound2: Int): Int {
    return if (bound1 <= bound2) {
        min(max(this, bound1), bound2)
    } else {
        min(max(this, bound2), bound1)
    }
}

fun Float.limit(bound1: Float, bound2: Float): Float {
    return if (bound1 <= bound2) {
        min(max(this, bound1), bound2)
    } else {
        min(max(this, bound2), bound1)
    }
}

fun Float.relativeLimit(bound1: Float, bound2: Float): Float {
    return if (bound1 <= bound2) {
        min(max(this, bound1), bound2) - bound1
    } else {
        min(max(this, bound2), bound1) - bound2
    }
}