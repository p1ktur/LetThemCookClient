package com.letthemcook.editor.domain.editor.components.prototype

sealed interface Relation {
    data object Left : Relation
    data object Top : Relation
    data object Right : Relation
    data object Bottom : Relation
}
