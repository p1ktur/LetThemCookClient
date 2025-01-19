package com.letthemcook.editor.domain.editor.components.containment

interface Relation {
    val lengthFromEdge: Float
    val isNear: Boolean
    val isOutside: Boolean
}