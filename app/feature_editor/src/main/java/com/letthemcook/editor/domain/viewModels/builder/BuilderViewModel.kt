package com.letthemcook.editor.domain.viewModels.builder

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.domain.list.forEachReversed
import com.letthemcook.core.domain.list.forEachReversedIndexed
import com.letthemcook.core.domain.list.swapWithLast
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.block.BlockContainment
import com.letthemcook.editor.domain.editor.components.block.unused.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.Relation
import com.letthemcook.editor.domain.editor.components.prototype.definePointRelation
import com.letthemcook.editor.domain.editor.components.prototype.findInHierarchy
import com.letthemcook.editor.domain.editor.components.prototype.getPointerContainer
import com.letthemcook.editor.domain.editor.components.prototype.insertBottomComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertLeftComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertRightComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertTopComponent
import com.letthemcook.editor.domain.editor.components.prototype.pointInBounds
import com.letthemcook.editor.domain.editor.components.prototype.removeFromHierarchy
import com.letthemcook.editor.domain.editor.geometry.limit
import com.letthemcook.editor.ui.components.popups.BlockEditorState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init

class BuilderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BuilderUiState())
    val uiState = _uiState.asStateFlow()

    private var retainProductOnBlock = false

    private val components get() = listOf(uiState.value.centralComponent)

    private var pointerDownJob: Job? = null

    init {
        //TODO load recipes from followed users
    }

    fun onUiAction(action: BuilderUiAction): Any? {
        when (action) {
            BuilderUiAction.NavigateBack -> Unit
            BuilderUiAction.NavigateToTutorial -> Unit

            // Common
            BuilderUiAction.FetchData -> fetchData()
            BuilderUiAction.SaveChanges -> Unit

            // Products
            is BuilderUiAction.AddProduct -> return addProduct(action.product, action.position)

            // Components
            is BuilderUiAction.AddUnusedComponent -> addUnusedComponent(action.unusedBlockComponent)
            is BuilderUiAction.UpdateUnusedComponent -> updateUnusedComponent(action.oldComponent, action.newComponent)

            is BuilderUiAction.AddComponent -> addComponent(action.unusedBlockComponent, action.blockComponent, action.position)
            is BuilderUiAction.UpdateComponent -> updateComponent(action.oldComponent, action.newComponent)
            is BuilderUiAction.RemoveComponent -> removeComponent(action.index)

            // Canvas actions
            is BuilderUiAction.UpdateCanvasSize -> updateCanvasSize(action.size)

            // Pointer actions
            is BuilderUiAction.PointerDown -> pointerDown(action.offset)
            is BuilderUiAction.PointerMove -> pointerMove(action.offset, action.deltaOffset)
            is BuilderUiAction.PointerRelease -> pointerRelease()
            is BuilderUiAction.PointerZoom -> pointerZoom(action.zoom)

            // Dragging
            is BuilderUiAction.SetDraggingState -> setDraggingState(action.state)

            // Block Editor
            is BuilderUiAction.SetBlockEditorState -> setBlockEditorState(action.state)
        }

        return null
    }

    // ACTIONS
    // Common

    private fun fetchData() {
//        if (serverRepository.authenticatedType.value is AuthType.Student) return
//
//        taskId?.let { id ->
//            viewModelScope.launch(Dispatchers.IO) {
//                val task = serverRepository.getTask(id)
//
//                if (task != null && task.diagramJson.length > 5) {
//                    val decodedDiagramJson = URLDecoder.decode(task.diagramJson, "utf-8")
//
//                    val saveData = ServerJson.get().decodeFromString<SaveData>(decodedDiagramJson)
//                    applySaveData(saveData)
//                }
//            }
//        }
    }

    // Products

    private fun addProduct(product: ProductItemData, position: Offset): BlockComponent? {
        uiState.value.blockComponents.forEachReversed { it ->
            when (it.containsPointer(scaleAndTranslate(position), false)) {
                BlockContainment.None -> Unit
                BlockContainment.Whole, is BlockContainment.ProductLabel -> {
                    _uiState.update {
                        it.copy(
                            unusedProducts = it.unusedProducts.minus(product)
                        )
                    }
                    it.productNames.add(product)
                    updateCanvasCounter()
                    return it
                }
            }
        }

        return null
    }

    // Components

    private fun addUnusedComponent(unusedBlockComponent: UnusedBlockComponent) {
        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents + unusedBlockComponent
            )
        }
    }

    private fun updateUnusedComponent(oldComponent: UnusedBlockComponent, newComponent: UnusedBlockComponent) {
        val oldComponentIndex = uiState.value.unusedBlockComponents.indexOf(oldComponent)

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents.toMutableList().apply {
                    set(oldComponentIndex, newComponent)
                }
            )
        }
    }

    private fun addComponent(unusedBlockComponent: UnusedBlockComponent, blockComponent: BlockComponent, position: Offset) {
        val component = when (uiState.value.centralComponent) {
            is ComposedComponent -> components.getPointerContainer(scaleAndTranslate(position))
            else -> uiState.value.centralComponent
        } ?: uiState.value.centralComponent

        when (component) {
            is BlockComponent, is ComposedComponent -> {
                val relation = component.definePointRelation(scaleAndTranslate(position))

                val newComponent = when (relation) {
                    Relation.Left -> {
                        component.insertLeftComponent(blockComponent)
                    }
                    Relation.Top -> {
                        component.insertTopComponent(blockComponent)
                    }
                    Relation.Right -> {
                        component.insertRightComponent(blockComponent)
                    }
                    Relation.Bottom -> {
                        component.insertBottomComponent(blockComponent)
                    }
                }

                if (uiState.value.centralComponent == component) {
                    newComponent?.let { com ->
                        _uiState.update {
                            it.copy(
                                centralComponent = com
                            )
                        }
                    }
                }
            }
            is EmptyComponent -> {
                _uiState.update {
                    it.copy(
                        centralComponent = blockComponent
                    )
                }
            }
        }

        _uiState.update {
            it.copy(
                blockComponents = it.blockComponents + blockComponent,
                unusedBlockComponents = it.unusedBlockComponents.minus(unusedBlockComponent),
                canvasCounter = it.canvasCounter + 1
            )
        }
    }

    private fun updateComponent(oldComponent: BlockComponent, newComponent: UnusedBlockComponent) {
        val oldComponentIndex = uiState.value.blockComponents.indexOf(oldComponent)

        (components.findInHierarchy(oldComponent) as? BlockComponent)?.let { component ->
            component.name = newComponent.name
            component.time = newComponent.time
            component.description = newComponent.description

            _uiState.update {
                it.copy(
                    blockComponents = it.blockComponents.toMutableList().apply {
                        set(oldComponentIndex, component)
                    }
                )
            }
        }
    }

    // TODO add settings to retain product on the block
    // TODO add settings to show debug (helper) rectangles on containers

    private fun removeComponent(index: Int) {
        val componentToRemove = uiState.value.blockComponents[index]

        if (!retainProductOnBlock) {
            _uiState.update {
                it.copy(
                    unusedProducts = it.unusedProducts + componentToRemove.productNames
                )
            }
            componentToRemove.productNames.clear()
        }

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents + componentToRemove.toUnusedBlockComponent(),
                blockComponents = it.blockComponents.minus(componentToRemove)
            )
        }

        if (uiState.value.centralComponent == componentToRemove) {
            _uiState.update {
                it.copy(
                    centralComponent = EmptyComponent
                )
            }
        } else {
            componentToRemove.removeFromHierarchy { newComponent ->
                _uiState.update {
                    it.copy(
                        centralComponent = newComponent
                    )
                }
            }
        }

        updateCanvasCounter()
    }

    // Canvas actions

    private fun updateCanvasSize(size: Size) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    size = Size(size.width, size.height),
                    center = Offset(size.width / 2f, size.height / 2f)
                ),
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

            uiState.value.blockComponents.forEachReversedIndexed { index, it ->
                when (val containment = it.containsPointer(scaleAndTranslate(offset), true)) {
                    BlockContainment.None -> Unit
                    BlockContainment.Whole -> {
                        pressOnComponent(index)
                        return@launch
                    }
                    is BlockContainment.ProductLabel -> {
                        val product = it.productNames.removeAt(containment.index)

                        _uiState.update {
                            it.copy(
                                movedProducts = it.movedProducts + product
                            )
                        }
                        return@launch
                    }
                }
            }

            clearComponentFocus()
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

        processPointerMovement()
    }

    private fun pointerRelease() {
        if (pointerDownJob?.isActive == true) {
            uiState.value.canvasUiState.pointerDownOffset?.let { pointerDownOffset ->
                val containerBlockComponent = components.getPointerContainer(
                    point = scaleAndTranslate(pointerDownOffset),
                    onlyBlocks = true,
                    strict = true
                )

                if (containerBlockComponent != null && containerBlockComponent is BlockComponent) {
                    setBlockEditorState(BlockEditorState.EditingBlock(containerBlockComponent))
                }

                pointerDownJob?.cancel()
                pointerDownJob = null
                return
            }
        }

        pointerDownJob?.cancel()
        pointerDownJob = null

        when {
            uiState.value.movedProducts.isNotEmpty() -> {
                val movedProduct = uiState.value.movedProducts.last()
                val productAdded = addProduct(
                    product = movedProduct,
                    position = uiState.value.canvasUiState.pointerMoveOffset ?:
                    uiState.value.canvasUiState.pointerDownOffset ?:
                    Offset.Zero
                ) != null

                if (!productAdded) {
                    _uiState.update {
                        it.copy(
                            unusedProducts = it.unusedProducts + movedProduct
                        )
                    }
                }
                _uiState.update {
                    it.copy(
                        movedProducts = emptyList()
                    )
                }
            }
            uiState.value.componentFocus == BuilderUiState.ComponentFocus.BLOCK && uiState.value.blockComponents.isNotEmpty() -> {
                val pointerMoveOffset = uiState.value.canvasUiState.pointerMoveOffset ?: Offset.Zero
                val containerBlockComponent = components.getPointerContainer(scaleAndTranslate(pointerMoveOffset), true)

                if (containerBlockComponent != null && containerBlockComponent != uiState.value.blockComponents.last()) {
                    val relation = containerBlockComponent.definePointRelation(scaleAndTranslate(pointerMoveOffset))

                    uiState.value.blockComponents.last().removeFromHierarchy { newComponent ->
                        _uiState.update {
                            it.copy(
                                centralComponent = newComponent
                            )
                        }
                    }

                    changeComponentPosition(containerBlockComponent, relation)
                }
            }
        }

        uiState.value.blockComponents.forEach { it.removeHighlight() }

        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    pointerDownOffset = null,
                    pointerMoveOffset = null,
                    pointerMoveDeltaOffset = null
                )
            )
        }

        clearComponentFocus()
    }

    private fun pointerZoom(zoom: Float) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    zoom = (it.canvasUiState.zoom * zoom).limit(0.8f, 1.8f)
                )
            )
        }
    }

    // Dragging actions

    private fun setDraggingState(state: DraggingState) {
        _uiState.update {
            it.copy(
                draggingState = state
            )
        }

        updateCanvasCounter()
    }

    // Block Editor

    private fun setBlockEditorState(state: BlockEditorState) {
        _uiState.update {
            it.copy(
                blockEditorState = state
            )
        }
    }

    // NOT ACTIONS
    // Components

    private fun changeComponentPosition(target: Component, relation: Relation) {
        if (uiState.value.blockComponents.isEmpty()) return
        val blockToInsert = uiState.value.blockComponents.last()

        if (target is BlockComponent) {
            val newComponent = when (relation) {
                Relation.Left -> {
                    target.insertLeftComponent(blockToInsert)
                }
                Relation.Top -> {
                    target.insertTopComponent(blockToInsert)
                }
                Relation.Right -> {
                    target.insertRightComponent(blockToInsert)
                }
                Relation.Bottom -> {
                    target.insertBottomComponent(blockToInsert)
                }
            }

            if (uiState.value.centralComponent == target) {
                newComponent?.let { com ->
                    _uiState.update {
                        it.copy(
                            centralComponent = com
                        )
                    }
                }
            }
        }

        updateCanvasCounter()
    }

    private fun pressOnComponent(index: Int) {
        _uiState.update {
            it.copy(
                blockComponents = it.blockComponents.toMutableList().swapWithLast(index),
                componentFocus = BuilderUiState.ComponentFocus.BLOCK,
                canvasUiState = it.canvasUiState.copy(
                    pointerMoveDeltaOffset = null,
                    pointerMoveOffset = null
                )
            )
        }

        uiState.value.blockComponents.last().highlight()
    }

    // Canvas

    private fun updateCanvasOffset(offset: Offset) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    offset = offset
                )
            )
        }
    }

    private fun updateCanvasCounter() {
        _uiState.update {
            it.copy(
                canvasCounter = it.canvasCounter + 1
            )
        }
    }

    // Pointer

    private fun processPointerMovement() {
        val pointerDownOffset = uiState.value.canvasUiState.pointerDownOffset ?: uiState.value.canvasUiState.center
        val pointerMoveOffset = uiState.value.canvasUiState.pointerMoveOffset ?: pointerDownOffset
        val pointerMoveDeltaOffset = uiState.value.canvasUiState.pointerMoveDeltaOffset

        when {
            uiState.value.draggingState == DraggingState.BLOCK -> {
                val closestBlockComponent = components.getPointerContainer(scaleAndTranslate(pointerMoveOffset)) ?: uiState.value.centralComponent
                val relation = closestBlockComponent.definePointRelation(scaleAndTranslate(pointerMoveOffset))

                when (closestBlockComponent) {
                    is BlockComponent -> {
                        closestBlockComponent.shadeQuarterForNextFrame(relation)
                        closestBlockComponent.highlightForNextFrame()
                    }
                    is HorizontalComposedComponent -> {
                        closestBlockComponent.shadeQuarterForNextFrame(relation)
                        closestBlockComponent.highlightForNextFrame()
                    }
                    is VerticalComposedComponent -> {
                        closestBlockComponent.shadeQuarterForNextFrame(relation)
                        closestBlockComponent.highlightForNextFrame()
                    }
                }

                updateCanvasCounter()
                return
            }
            uiState.value.draggingState == DraggingState.PRODUCT -> {
                updateCanvasCounter()
                return
            }
            uiState.value.movedProducts.isNotEmpty() -> {
                updateCanvasCounter()
                return
            }
            uiState.value.componentFocus != BuilderUiState.ComponentFocus.NONE -> {
                when (uiState.value.componentFocus) {
                    BuilderUiState.ComponentFocus.BLOCK -> {
                        uiState.value.blockComponents.last().removeHighlight()

                        val containerBlockComponent = components.getPointerContainer(scaleAndTranslate(pointerMoveOffset), true)
                            ?: uiState.value.centralComponent

                        if (containerBlockComponent == uiState.value.blockComponents.last()) {
                            if (containerBlockComponent.pointInBounds(pointerMoveOffset)) {
                                containerBlockComponent.highlightForNextFrame()
                            }
                        } else {
                            val relation = containerBlockComponent.definePointRelation(scaleAndTranslate(pointerMoveOffset))

                            if (containerBlockComponent is BlockComponent) {
                                containerBlockComponent.shadeQuarterForNextFrame(relation)
                                containerBlockComponent.highlightForNextFrame()
                            }
                        }
                    }
                    BuilderUiState.ComponentFocus.NONE -> Unit
                }

                updateCanvasCounter()
                return
            }
            else -> if (pointerMoveDeltaOffset != Offset.Zero && pointerMoveDeltaOffset != null) {
                val canvasOffset = uiState.value.canvasUiState.cachedOffset +
                        (pointerMoveOffset - pointerDownOffset).div(uiState.value.canvasUiState.zoom)

                updateCanvasOffset(canvasOffset)
            }
        }

        updateCanvasCounter()
    }

    // Other

    private fun clearComponentFocus() {
        _uiState.update {
            it.copy(
                componentFocus = BuilderUiState.ComponentFocus.NONE
            )
        }

//        processPointerMovement()
    }

    private fun scaleAndTranslate(position: Offset): Offset = uiState.value.scaleAndTranslate(position)
}