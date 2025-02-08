package com.letthemcook.editor.domain.viewModels.cooking

import com.letthemcook.editor.domain.cooking.CookingState
import com.letthemcook.editor.domain.cooking.track.EmptyTrackData
import com.letthemcook.editor.domain.cooking.track.TrackData
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState

data class CookingUiState(
    // Data
    val ownerId: String,
    val recipeId: String,
    val recipeName: String,
    // Cooking
    val cookingState: CookingState = CookingState.NOT_STARTED,
    val totalCookingTime: Long = 0L,
    val cookingTimeLeft: Long = 0L,
    val trackData: TrackData = EmptyTrackData,
    val trackDataCounter: Int = 0,
    val cookingProgress: Float = 0f,
    // Components
    val selectedBlock: BlockComponent? = null,
    val startComponent: StartComponent = StartComponent(),
    val centralComponent: Component = EmptyComponent,
    val endComponent: EndComponent = EndComponent(),
    // Canvas data
    val canvasUiState: CanvasUiState = CanvasUiState(),
    val canvasCounter: Int = 0
)