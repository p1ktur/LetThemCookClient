package com.letthemcook.editor.domain.editor.geometry

import kotlin.math.*

fun Float.limit(bound1: Float, bound2: Float): Float {
    return if (bound1 <= bound2) {
        min(max(this, bound1), bound2)
    } else {
        min(max(this, bound2), bound1)
    }
}