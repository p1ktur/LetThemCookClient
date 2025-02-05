package com.letthemcook.editor.domain.viewModels.cooking

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.editor.domain.cooking.BlockCookingState
import com.letthemcook.editor.domain.cooking.CookingState
import com.letthemcook.editor.domain.cooking.track.EmptyTrackData
import com.letthemcook.editor.domain.cooking.track.doForEveryChild
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.block.BlockContainment
import com.letthemcook.editor.domain.editor.components.prototype.canFinish
import com.letthemcook.editor.domain.editor.components.prototype.canRestore
import com.letthemcook.editor.domain.editor.components.prototype.cook
import com.letthemcook.editor.domain.editor.components.prototype.countFinishedAndTotal
import com.letthemcook.editor.domain.editor.components.prototype.decreaseCookingTimer
import com.letthemcook.editor.domain.editor.components.prototype.doForEveryChild
import com.letthemcook.editor.domain.editor.components.prototype.finish
import com.letthemcook.editor.domain.editor.components.prototype.firstInHierarchy
import com.letthemcook.editor.domain.editor.components.prototype.getPointerContainer
import com.letthemcook.editor.domain.editor.components.prototype.getTotalTime
import com.letthemcook.editor.domain.editor.components.prototype.isCooking
import com.letthemcook.editor.domain.editor.components.prototype.isWaiting
import com.letthemcook.editor.domain.editor.components.prototype.restore
import com.letthemcook.editor.domain.editor.components.prototype.toTrackData
import com.letthemcook.editor.domain.editor.geometry.limit
import com.letthemcook.editor.domain.serialization.RecipeGraphSerializer
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction
import com.letthemcook.editor.ui.components.popups.BlockEditorState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CookingViewModel(
    recipeJson: String,
    private val recipeGraphSerializer: RecipeGraphSerializer
) : ViewModel() {

    private val _uiState = MutableStateFlow(CookingUiState())
    val uiState = _uiState.asStateFlow()

    private val components get() = listOf(uiState.value.centralComponent)

    private var cookingJob: Job? = null
    private var pointerDownJob: Job? = null

    init {
        viewModelScope.launch {
            val centralComponent = recipeGraphSerializer.deserializeComponent(recipeJson)
            val totalTime = centralComponent.getTotalTime()

            _uiState.update {
                it.copy(
                    centralComponent = centralComponent,
                    totalCookingTime = totalTime,
                    cookingTimeLeft = totalTime
                )
            }

            delay(100)
            updateCanvasCounter()
        }
    }

    fun onUiAction(action: CookingUiAction) {
        when (action) {
            CookingUiAction.NavigateBack -> Unit

            is CookingUiAction.ViewMediaFile -> Unit
            CookingUiAction.StopViewingMediaFile -> stopViewingMediaFile()

            // Cooking
            CookingUiAction.StartCooking -> startCooking()
            CookingUiAction.PauseCooking -> pauseCooking()
            CookingUiAction.StopCooking -> stopCooking()

            is CookingUiAction.FinishBlock -> finishBlock(action.ref)
            is CookingUiAction.SelectBlock -> selectBlock(action.ref)
            is CookingUiAction.RestoreBlock -> restoreBlock(action.ref)

            // Blocks
            CookingUiAction.DeselectBlock -> deselectBlock()

            // Canvas actions
            is CookingUiAction.UpdateCanvasSize -> updateCanvasSize(action.size)

            // Pointer actions
            is CookingUiAction.PointerDown -> pointerDown(action.offset)
            is CookingUiAction.PointerMove -> pointerMove(action.offset, action.deltaOffset)
            is CookingUiAction.PointerRelease -> pointerRelease()
            is CookingUiAction.PointerZoom -> pointerZoom(action.zoom)
        }
    }

    // ACTIONS

    private fun stopViewingMediaFile() {
        _uiState.update {
            it.copy(
                viewedMediaFile = null
            )
        }
    }

    // Cooking

    private fun startCooking() {
        _uiState.update {
            it.copy(
                cookingState = CookingState.STARTED
            )
        }

        if (uiState.value.trackData == EmptyTrackData) {
            uiState.value.centralComponent.firstInHierarchy().cook()
        } else {
            uiState.value.trackData.doForEveryChild {
                if (isWaiting()) cookingState = BlockCookingState.COOKING
            }
        }

        recalculateTrackData()

        cookingJob = viewModelScope.launch {
            while (isActive) {
                components.decreaseCookingTimer()

                if (uiState.value.cookingTimeLeft > 0L) {
                    _uiState.update {
                        it.copy(
                            cookingTimeLeft = it.cookingTimeLeft - 1000L
                        )
                    }
                }

                delay(1000L)
                updateCanvasCounter()
            }
        }

        updateCanvasCounter()
    }

    private fun pauseCooking() {
        cookingJob?.cancel()
        cookingJob = null

        _uiState.update {
            it.copy(
                cookingState = CookingState.PAUSED
            )
        }

        uiState.value.trackData.doForEveryChild {
            if (isCooking()) cookingState = BlockCookingState.WAITING
        }

        updateCanvasCounter()
    }

    private fun stopCooking() {
        cookingJob?.cancel()
        cookingJob = null

        _uiState.update {
            it.copy(
                cookingState = CookingState.NOT_STARTED,
                trackData = EmptyTrackData,
                cookingProgress = 0f,
                cookingTimeLeft = it.totalCookingTime
            )
        }

        components.doForEveryChild {
            restore(uiState.value.cookingState)
        }

        updateCanvasCounter()
    }

    private fun finishBlock(ref: BlockComponent) {
        if (uiState.value.cookingState != CookingState.STARTED || !ref.canFinish()) return

        ref.finish()

        val (finished, total) = uiState.value.centralComponent.countFinishedAndTotal()

        _uiState.update {
            it.copy(
                cookingProgress = finished.toFloat() / total
            )
        }

        recalculateTrackData()

        updateCanvasCounter()
    }

    private fun selectBlock(ref: BlockComponent) {
        _uiState.update {
            it.copy(
                selectedBlock = ref
            )
        }
    }

    private fun restoreBlock(ref: BlockComponent) {
        if (uiState.value.cookingState != CookingState.STARTED || !ref.canRestore()) return

        ref.restore(uiState.value.cookingState)

        val (finished, total) = uiState.value.centralComponent.countFinishedAndTotal()

        _uiState.update {
            it.copy(
                cookingProgress = finished.toFloat() / total
            )
        }

        recalculateTrackData()

        updateCanvasCounter()
    }

    // Blocks

    private fun deselectBlock() {
        _uiState.update {
            it.copy(
                selectedBlock = null
            )
        }
    }

    // Canvas actions

    private fun updateCanvasSize(size: Size) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    size = Size(size.width, size.height),
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            )
        }

        updateCanvasCounter()
    }

    // Pointer actions

    private fun pointerDown(offset: Offset) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    cachedOffset = it.canvasUiState.offset,
                    pointerDownOffset = offset
                )
            )
        }

        pointerDownJob = viewModelScope.launch {
            delay(200)
        }
    }

    private fun pointerMove(offset: Offset, deltaOffset: Offset) {
        pointerDownJob?.cancel()
        pointerDownJob = null

        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    pointerMoveOffset = offset,
                    pointerMoveDeltaOffset = deltaOffset
                )
            )
        }

        val pointerDownOffset = uiState.value.canvasUiState.pointerDownOffset ?: uiState.value.canvasUiState.center
        val pointerMoveOffset = uiState.value.canvasUiState.pointerMoveOffset ?: pointerDownOffset
        val pointerMoveDeltaOffset = uiState.value.canvasUiState.pointerMoveDeltaOffset

        if (pointerMoveDeltaOffset != Offset.Zero && pointerMoveDeltaOffset != null) {
            val canvasOffset = uiState.value.canvasUiState.cachedOffset + (pointerMoveOffset - pointerDownOffset).div(uiState.value.canvasUiState.zoom)

            _uiState.update {
                it.copy(
                    canvasUiState = it.canvasUiState.copy(
                        offset = canvasOffset
                    )
                )
            }
        }

        updateCanvasCounter()
    }

    private fun pointerRelease() {
        if (pointerDownJob?.isActive == true) {
            uiState.value.canvasUiState.pointerDownOffset?.let { pointerDownOffset ->
                val checkOffset = scaleAndTranslate(pointerDownOffset)
                val containerBlockComponent = components.getPointerContainer(
                    point = checkOffset,
                    onlyBlocks = true,
                    strict = true
                )

                if (containerBlockComponent != null && containerBlockComponent is BlockComponent) {
                    val containment = containerBlockComponent.containsPointer(checkOffset, true)

                    if (containment == BlockContainment.FileIcon) {
                        _uiState.update {
                            it.copy(
                                viewedMediaFile = containerBlockComponent.file
                            )
                        }
                    } else {
                        selectBlock(containerBlockComponent)
                    }
                }

                pointerDownJob?.cancel()
                pointerDownJob = null
                return
            }
        }

        pointerDownJob?.cancel()
        pointerDownJob = null

        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    pointerDownOffset = null,
                    pointerMoveOffset = null,
                    pointerMoveDeltaOffset = null
                )
            )
        }
    }

    private fun pointerZoom(zoom: Float) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    zoom = (it.canvasUiState.zoom * zoom).limit(0.4f, 1.8f)
                )
            )
        }
    }

    // NOT ACTIONS
    // Canvas

    private fun updateCanvasCounter() {
        _uiState.update {
            it.copy(
                canvasCounter = it.canvasCounter + 1
            )
        }
    }

    // Track data

    private fun recalculateTrackData() {
        _uiState.update {
            it.copy(
                trackData = uiState.value.centralComponent.toTrackData(),
                trackDataCounter = it.trackDataCounter + 1
            )
        }
    }

    // Other

    private fun scaleAndTranslate(position: Offset): Offset = uiState.value.canvasUiState.scaleAndTranslate(position)
}