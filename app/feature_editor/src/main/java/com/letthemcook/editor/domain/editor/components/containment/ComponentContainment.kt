package com.letthemcook.editor.domain.editor.components.containment

sealed interface ComponentContainment {
    data object None : ComponentContainment
    data object Whole : ComponentContainment
    data class ProductLabel(val index: Int) : ComponentContainment

    data class Left(
        override val lengthFromEdge: Float,
        override val isNear: Boolean = lengthFromEdge < 64f,
        override val isOutside: Boolean = lengthFromEdge > 0f
    ) : ComponentContainment, Relation
    data class Top(
        override val lengthFromEdge: Float,
        override val isNear: Boolean = lengthFromEdge < 64f,
        override val isOutside: Boolean = lengthFromEdge > 0f
    ) : ComponentContainment, Relation
    data class Right(
        override val lengthFromEdge: Float,
        override val isNear: Boolean = lengthFromEdge < 64f,
        override val isOutside: Boolean = lengthFromEdge > 0f
    ) : ComponentContainment, Relation
    data class Bottom(
        override val lengthFromEdge: Float,
        override val isNear: Boolean = lengthFromEdge < 64f,
        override val isOutside: Boolean = lengthFromEdge > 0f
    ) : ComponentContainment, Relation
}