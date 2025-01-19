package com.letthemcook.editor.domain.viewModels.builder

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import com.letthemcook.core.domain.list.forEachReversed
import com.letthemcook.core.domain.list.forEachReversedIndexed
import com.letthemcook.core.domain.list.swapWithLast
import com.letthemcook.core.domain.model.data.ProductItemData
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.BlockComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.unused.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.domain.editor.components.containment.ComponentContainment
import com.letthemcook.editor.domain.editor.components.prototype.definePointRelation
import com.letthemcook.editor.domain.editor.components.prototype.getClosestToThePoint
import com.letthemcook.editor.domain.editor.components.prototype.insertBottomComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertLeftComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertRightComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertTopComponent
import com.letthemcook.editor.domain.editor.components.prototype.moveTo
import com.letthemcook.editor.domain.editor.components.prototype.removeComponent
import com.letthemcook.editor.domain.editor.geometry.limit
import com.letthemcook.editor.domain.editor.geometry.zoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BuilderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BuilderUiState())
    val uiState = _uiState.asStateFlow()

    private var retainProductOnBlock = false

    // Components without Start and End components
    private val components: List<Component> get() = uiState.value.startComponent.getChainUntilEnd()

    init {
        //TODO load recipes from followed users
    }

    fun onUiAction(action: BuilderUiAction) {
        when (action) {
            BuilderUiAction.NavigateBack -> Unit
            BuilderUiAction.NavigateToTutorial -> Unit

            // Common
            BuilderUiAction.FetchData -> fetchData()
            BuilderUiAction.SaveChanges -> Unit

            // Products
            is BuilderUiAction.AddProduct -> addProduct(action.product, action.position)

            // Components
            is BuilderUiAction.AddUnusedComponent -> addUnusedComponent(action.unusedBlockComponent)
            is BuilderUiAction.AddComponent -> addComponent(action.unusedBlockComponent, action.position, action.size)
            is BuilderUiAction.UpdateComponentData -> updateComponent(action.updater)
            is BuilderUiAction.RemoveComponent -> removeComponent(action.index)

            // Canvas actions
            is BuilderUiAction.UpdateCanvasSize -> updateCanvasSize(action.size)

            // Pointer actions
            is BuilderUiAction.PointerDown -> pointerDown(action.offset)
            is BuilderUiAction.PointerMove -> pointerMove(action.offset, action.deltaOffset, action.draggingState)
            is BuilderUiAction.PointerRelease -> pointerRelease()
            is BuilderUiAction.PointerZoom -> pointerZoom(action.zoom)
        }
    }

    // ACTIONS
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

    private fun addProduct(product: ProductItemData, position: Offset): Boolean {
        uiState.value.blockComponents.forEachReversed { it ->
            when (it.containsPointer(position.scaledAndTranslated(), false)) {
                ComponentContainment.None -> Unit
                ComponentContainment.Whole, is ComponentContainment.ProductLabel -> {
                    _uiState.update {
                        it.copy(
                            unusedProducts = it.unusedProducts.minus(product)
                        )
                    }
                    it.productNames.add(product)
                    updateCanvasCounter()
                    return true
                }
                is ComponentContainment.Left -> Unit
                is ComponentContainment.Top -> Unit
                is ComponentContainment.Right -> Unit
                is ComponentContainment.Bottom -> Unit
            }
        }

        return false
    }

    private fun addUnusedComponent(unusedBlockComponent: UnusedBlockComponent) {
        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents + unusedBlockComponent,
                canvasCounter = it.canvasCounter + 1
            )
        }
    }

    private fun addComponent(unusedBlockComponent: UnusedBlockComponent, position: Offset, size: Size) {
        val blockComponent = BlockComponent(
            name = unusedBlockComponent.name,
            description = unusedBlockComponent.description,
            time = unusedBlockComponent.time,
            productNames = unusedBlockComponent.productNames,
            prevComponent = EmptyComponent,
            nextComponent = EmptyComponent,
            size = size
        )

        try {
            if (components.isNotEmpty()) {
                val closestBlockComponent = components.getClosestToThePoint(position.scaledAndTranslated())

                closestBlockComponent?.definePointRelation(position.scaledAndTranslated())?.let { relation ->
                    when (relation) {
                        is ComponentContainment.Left -> {
                            closestBlockComponent.insertLeftComponent(blockComponent)
                        }
                        is ComponentContainment.Top -> {
                            closestBlockComponent.insertTopComponent(blockComponent)
                        }
                        is ComponentContainment.Right -> {
                            closestBlockComponent.insertRightComponent(blockComponent)
                        }
                        is ComponentContainment.Bottom -> {
                            closestBlockComponent.insertBottomComponent(blockComponent)
                        }
                    }
                }
            } else {
                uiState.value.startComponent.insertBottomComponent(blockComponent)
            }

            _uiState.update {
                it.copy(
                    blockComponents = it.blockComponents + blockComponent,
                    unusedBlockComponents = it.unusedBlockComponents.minus(unusedBlockComponent),
                    canvasCounter = it.canvasCounter + 1
                )
            }
        } catch (_: Exception) { }
    }

    private fun updateComponent(updater: BlockComponent.() -> Unit) {
        uiState.value.blockComponents.last().apply(updater)

        updateCanvasCounter()
    }

    // TODO add settings to retain product on the block
    // TODO add settings to show debug (helper) rectangles on containers

    private fun removeComponent(index: Int) {
        val componentToDelete = uiState.value.blockComponents[index]

        // TODO connect two component which were around this one

        if (!retainProductOnBlock) {
            _uiState.update {
                it.copy(
                    unusedProducts = it.unusedProducts + componentToDelete.productNames
                )
            }
            componentToDelete.productNames.clear()
        }

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents + componentToDelete.toUnusedBlockComponent(),
                blockComponents = it.blockComponents.minus(componentToDelete)
            )
        }

        componentToDelete.removeComponent()

        updateCanvasCounter()
    }

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

    private fun pointerDown(offset: Offset) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    cachedOffset = it.canvasUiState.offset,
                    pointerDownOffset = offset
                )
            )
        }

        uiState.value.blockComponents.forEachReversedIndexed { index, it ->
            when (val containment = it.containsPointer(offset.scaledAndTranslated(), true)) {
                ComponentContainment.None -> Unit
                ComponentContainment.Whole -> {
                    clickOnComponent(index)
                    return
                }
                is ComponentContainment.ProductLabel -> {
                    val product = it.productNames.removeAt(containment.index)

                    _uiState.update {
                        it.copy(
                            movedProducts = it.movedProducts + product
                        )
                    }
                    return
                }
                is ComponentContainment.Left -> Unit
                is ComponentContainment.Top -> Unit
                is ComponentContainment.Right -> Unit
                is ComponentContainment.Bottom -> Unit
            }
        }

//        if (uiState.value.startComponent.containsPointerWhole(offset.scaledAndTranslated(), true)) {
//            _uiState.update {
//                it.copy(
//                    componentFocus = BuilderUiState.ComponentFocus.START,
//                    canvasCounter = it.canvasCounter + 1
//                )
//            }
//            return
//        }
//
//        if (uiState.value.endComponent.containsPointerWhole(offset.scaledAndTranslated(), true)) {
//            _uiState.update {
//                it.copy(
//                    componentFocus = BuilderUiState.ComponentFocus.END,
//                    canvasCounter = it.canvasCounter + 1
//                )
//            }
//            return
//        }

//        uiState.value.blockConnections.forEachReversedIndexed { index, it ->
//            when (val containment = it.containsPointer(offset.scaledAndTranslated())) {
//                ConnectionContainmentResult.None -> Unit
//                else -> {
//                    clickOnConnection(index, containment)
//                    return
//                }
//            }
//        }

        clearAllFocuses()
    }

    private fun pointerMove(offset: Offset, deltaOffset: Offset, draggingState: DraggingState) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    pointerMoveOffset = offset,
                    pointerMoveDeltaOffset = deltaOffset
                )
            )
        }

        processPointerMovement(draggingState)
    }

    private fun pointerRelease() {
        if (uiState.value.movedProducts.isNotEmpty()) {
            val movedProduct = uiState.value.movedProducts.last()
            val productAdded = addProduct(
                movedProduct,
                uiState.value.canvasUiState.pointerMoveOffset ?:
                uiState.value.canvasUiState.pointerDownOffset ?:
                Offset.Zero
            )

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

        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    pointerDownOffset = null,
                    pointerMoveOffset = null,
                    pointerMoveDeltaOffset = null
                )
            )
        }

        clearComponentFocuses()
    }

    private fun pointerZoom(zoom: Float) {
        _uiState.update {
            it.copy(
                canvasUiState = it.canvasUiState.copy(
                    zoom = (it.canvasUiState.zoom * zoom).limit(0.5f, 2f)
                )
            )
        }
    }

    // NOT ACTIONS

    private fun clickOnComponent(index: Int) {
        _uiState.update {
            it.copy(
                blockComponents = it.blockComponents.toMutableList().swapWithLast(index),
                componentFocus = BuilderUiState.ComponentFocus.BLOCK,
                canvasCounter = it.canvasCounter + 1,
                canvasUiState = it.canvasUiState.copy(
                    pointerMoveDeltaOffset = null,
                    pointerMoveOffset = null
                )
            )
        }

        processPointerMovement()
    }

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


    private fun clearAllFocuses() {
        uiState.value.blockComponents.forEach { it.clearHighlight() }

        _uiState.update {
            it.copy(
                componentFocus = BuilderUiState.ComponentFocus.NONE
            )
        }

        processPointerMovement()
    }

    private fun clearComponentFocuses() {
        _uiState.update {
            it.copy(
                componentFocus = BuilderUiState.ComponentFocus.NONE
            )
        }

        processPointerMovement()
    }

    // TODO leave phantom and be able to drag blocks as it is unused
    // TODO also if i want to leave block where it is i can see such possibility by highlighting whole phantom

    private fun processPointerMovement(draggingState: DraggingState = DraggingState.NONE) {
        val pointerDownOffset = uiState.value.canvasUiState.pointerDownOffset ?: uiState.value.canvasUiState.center
        val pointerMoveOffset = uiState.value.canvasUiState.pointerMoveOffset ?: uiState.value.canvasUiState.center
        val pointerMoveDeltaOffset = uiState.value.canvasUiState.pointerMoveDeltaOffset ?: return

        when {
            draggingState == DraggingState.BLOCK -> {
                val closestBlockComponent = components.getClosestToThePoint(pointerMoveOffset.scaledAndTranslated())

                closestBlockComponent?.definePointRelation(pointerMoveOffset.scaledAndTranslated())?.let { relation ->
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
                }

                return
            }
            draggingState == DraggingState.PRODUCT -> {
                return
            }
            uiState.value.movedProducts.isNotEmpty() -> {
                updateCanvasCounter()
                return
            }
            uiState.value.componentFocus != BuilderUiState.ComponentFocus.NONE -> {
                when (uiState.value.componentFocus) {
                    BuilderUiState.ComponentFocus.BLOCK -> if (uiState.value.blockComponents.isNotEmpty()) {
                        uiState.value.blockComponents.last().moveTo(pointerMoveOffset.scaledAndTranslated())
                        updateCanvasCounter()
                        return
                    }
                    BuilderUiState.ComponentFocus.START -> {
                        uiState.value.startComponent.moveTo(pointerMoveOffset.scaledAndTranslated())
                        updateCanvasCounter()
                        return
                    }
                    BuilderUiState.ComponentFocus.END -> {
                        uiState.value.endComponent.moveTo(pointerMoveOffset.scaledAndTranslated())
                        updateCanvasCounter()
                        return
                    }
                    BuilderUiState.ComponentFocus.NONE -> Unit
                }
            }
            else -> {
                val canvasOffset = uiState.value.canvasUiState.cachedOffset +
                        (pointerMoveOffset - pointerDownOffset).div(uiState.value.canvasUiState.zoom)

                updateCanvasOffset(canvasOffset)
            }
        }

        if (pointerMoveDeltaOffset == Offset.Zero) {
            uiState.value.blockComponents.forEachReversedIndexed { index, it ->
                when (it.containsPointer(pointerDownOffset.scaledAndTranslated(), false)) {
                    ComponentContainment.None -> Unit
                    ComponentContainment.Whole -> {
                        uiState.value.blockComponents.forEachIndexed { i, com -> if (i != index) com.clearHighlight() }
                        updateCanvasCounter()
                        return
                    }
                    is ComponentContainment.ProductLabel -> Unit
                    is ComponentContainment.Left -> Unit
                    is ComponentContainment.Top -> Unit
                    is ComponentContainment.Right -> Unit
                    is ComponentContainment.Bottom -> Unit
                }
            }
        }

//            uiState.value.blockConnections.forEachReversedIndexed { index, it ->
//                when (val containment = it.containsPointer(pointerDownOffset.scaledAndTranslated())) {
//                    ConnectionContainmentResult.None -> Unit
//                    ConnectionContainmentResult.Whole -> {
//                        uiState.value.blockConnections.forEachIndexed { i, con -> if (i != index) con.clearHighlight() }
//                        uiState.value.blockComponents.forEach { com -> com.clearHighlight() }
//                        updateCanvasCounter()
//                        return
//                    }
//                }
//            }

        updateCanvasCounter()
    }

    private fun Offset.scaledAndTranslated(): Offset {
        return zoom(uiState.value.canvasUiState.center, uiState.value.canvasUiState.zoom) - uiState.value.canvasUiState.offset
    }
}