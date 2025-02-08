package com.letthemcook.core.domain.list

inline fun <T> List<T>.forEachReversed(action: (T) -> Unit) {
    for (i in size - 1 downTo 0) {
        action(get(i))
    }
}

fun <T> List<T>.filterOn(other: List<T>): List<T> {
    val filtered = filter { other.contains(it) }
    return filtered + other.filterNot { contains(it) }
}