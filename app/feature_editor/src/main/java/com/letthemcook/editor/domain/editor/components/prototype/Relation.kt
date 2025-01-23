package com.letthemcook.editor.domain.editor.components.prototype

sealed interface Relation {
    data object Left : Relation
    data object Top : Relation
    data object Right : Relation
    data object Bottom : Relation

    fun isHorizontal(): Boolean {
        return this == Left || this == Right
    }

    fun isVertical(): Boolean {
        return this == Top || this == Bottom
    }
}
