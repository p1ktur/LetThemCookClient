package com.letthemcook.editor.domain.editor.components.block

sealed interface BlockContainment {
    data object None : BlockContainment
    data object Whole : BlockContainment
    data class ProductLabel(val index: Int) : BlockContainment
}