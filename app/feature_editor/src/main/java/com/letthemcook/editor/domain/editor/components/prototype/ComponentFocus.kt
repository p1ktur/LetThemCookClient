package com.letthemcook.editor.domain.editor.components.prototype

import com.letthemcook.editor.domain.editor.components.block.BlockComponent

sealed interface ComponentFocus {
    data object None : ComponentFocus
    data class Block(val ref: BlockComponent) : ComponentFocus
    data class Product(val data: com.letthemcook.core.domain.model.recipe.Product) : ComponentFocus
}