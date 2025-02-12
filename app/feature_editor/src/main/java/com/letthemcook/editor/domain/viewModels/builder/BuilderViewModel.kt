package com.letthemcook.editor.domain.viewModels.builder

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.remote.WeightedProduct
import com.letthemcook.editor.data.UnusedBlocksDao
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.block.BlockContainment
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.ComponentFocus
import com.letthemcook.editor.domain.editor.components.prototype.Relation
import com.letthemcook.editor.domain.editor.components.prototype.definePointRelation
import com.letthemcook.editor.domain.editor.components.prototype.doForEveryChild
import com.letthemcook.editor.domain.editor.components.prototype.findInHierarchy
import com.letthemcook.editor.domain.editor.components.prototype.getPointerContainer
import com.letthemcook.editor.domain.editor.components.prototype.getTotalTime
import com.letthemcook.editor.domain.editor.components.prototype.insertBottomComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertLeftComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertRightComponent
import com.letthemcook.editor.domain.editor.components.prototype.insertTopComponent
import com.letthemcook.editor.domain.editor.components.prototype.pointInBounds
import com.letthemcook.editor.domain.editor.components.prototype.removeFromHierarchy
import com.letthemcook.editor.domain.editor.geometry.limit
import com.letthemcook.editor.domain.serialization.RecipeGraphSerializer
import com.letthemcook.editor.ui.components.popups.BlockEditorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuilderViewModel(
    ownerId: String,
    recipeId: String,
    recipeJson: String?,
    recipeName: String,
    weightedProducts: List<WeightedProduct>,
    private val recipeGraphSerializer: RecipeGraphSerializer,
    private val unusedBlocksDao: UnusedBlocksDao,
    localDataManager: LocalDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuilderUiState(ownerId, recipeId, recipeName))
    val uiState = _uiState.asStateFlow()

    private var retainProductOnBlock = false

    private val components get() = listOf(uiState.value.centralComponent)

    private var pointerDownJob: Job? = null

    var viewMediaFile: ((String, File) -> Unit)? = null

    init {
        val unusedProducts = mutableListOf<WeightedProduct>()

        weightedProducts.forEach { product ->
            if (product.amount == 0) {
                unusedProducts.add(product)
            } else {
                repeat(product.amount) {
                    unusedProducts.add(product.copy(amount = 0))
                }
            }
        }

        if (recipeJson != null) {
            viewModelScope.launch {
                val centralComponent = recipeGraphSerializer.deserializeComponent(recipeJson)

                _uiState.update {
                    it.copy(
                        centralComponent = centralComponent
                    )
                }

                components.doForEveryChild {
                    (this as? BlockComponent)?.let { component ->
                        val toRemove = mutableListOf<WeightedProduct>()

                        component.products.forEach { product ->
                            val index = unusedProducts.indexOfFirst { it.data.id == product.data.id }
                            if (index >= 0) {
                                unusedProducts.removeAt(index)
                            } else {
                                toRemove.add(product)
                            }
                        }

                        component.products.removeAll(toRemove.toSet())
                    }
                }

                delay(100)
                updateCanvasCounter()
            }
        }

        _uiState.update {
            it.copy(
                unusedProducts = unusedProducts
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            // check for left undeleted blocks of deleted recipes
            val recipeIds = localDataManager.getRecipeIds()
            if (recipeIds.isNotEmpty()) {
                unusedBlocksDao.deleteLeftOnes(recipeIds)
            }

            _uiState.update {
                it.copy(
                    unusedBlockComponents = unusedBlocksDao.getBlocksByRecipeId(recipeId)
                )
            }
        }
    }

    fun onUiAction(action: BuilderUiAction): Any? {
        when (action) {
            BuilderUiAction.NavigateBack -> Unit
            BuilderUiAction.NavigateToTutorial -> Unit

            is BuilderUiAction.ViewMediaFile -> viewMediaFile?.invoke(action.blockId, action.file)

            // Common
            BuilderUiAction.TryDemoCooking -> Unit

            // Products
            is BuilderUiAction.AddProduct -> return addProduct(action.weightedProduct, action.position)

            // Components
            is BuilderUiAction.AddUnusedComponent -> addUnusedComponent(action.unusedBlockComponent)
            is BuilderUiAction.UpdateUnusedComponent -> updateUnusedComponent(action.oldComponent, action.newComponent)
            is BuilderUiAction.DeleteUnusedBlock -> deleteUnusedBlock(action.component)

            is BuilderUiAction.AddComponent -> addComponent(action.unusedBlockComponent, action.blockComponent, action.position)
            is BuilderUiAction.UpdateComponent -> updateComponent(action.oldComponent, action.newComponent)
            is BuilderUiAction.RemoveComponent -> removeComponent()
            is BuilderUiAction.DeleteBlock -> deleteBlock(action.component)

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

    fun getRecipeJson(): String {
        return if (uiState.value.centralComponent is EmptyComponent) {
            "null"
        } else {
            prepareCookingData().toString()
        }
    }

    fun getCookingTime(): Long {
        return uiState.value.centralComponent.getTotalTime()
    }

    fun prepareCookingData(): String? {
        return try {
            recipeGraphSerializer.serializeToJson(uiState.value.centralComponent)
        } catch (_: Exception) {
            null
        }
    }

    // ACTIONS

    // Products

    private fun addProduct(product: WeightedProduct, position: Offset): BlockComponent? {
        return getBlockUnderPosition(scaleAndTranslate(position))?.let { containedBlock ->
            _uiState.update {
                it.copy(
                    unusedProducts = it.unusedProducts.minus(product)
                )
            }

            containedBlock.products.add(product)
            containedBlock.tryRecalculateSize()

            updateCanvasCounter()
            containedBlock
        }
    }

    // Components

    private fun addUnusedComponent(unusedBlockComponent: UnusedBlockComponent) {
        viewModelScope.launch(Dispatchers.IO) {
            unusedBlocksDao.upsertUnusedBlock(unusedBlockComponent)
        }

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents + unusedBlockComponent
            )
        }
    }

    private fun updateUnusedComponent(oldComponent: UnusedBlockComponent, newComponent: UnusedBlockComponent) {
        val oldComponentIndex = uiState.value.unusedBlockComponents.indexOf(oldComponent)

        viewModelScope.launch(Dispatchers.IO) {
            unusedBlocksDao.upsertUnusedBlock(newComponent)
        }

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents.toMutableList().apply {
                    set(oldComponentIndex, newComponent)
                }
            )
        }
    }

    private fun deleteUnusedBlock(component: UnusedBlockComponent) {
        viewModelScope.launch(Dispatchers.IO) {
            unusedBlocksDao.deleteById(component.id)
        }

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents.minus(component)
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

        viewModelScope.launch(Dispatchers.IO) {
            unusedBlocksDao.deleteById(unusedBlockComponent.id)
        }

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents.minus(unusedBlockComponent),
                canvasCounter = it.canvasCounter + 1
            )
        }
    }

    private fun updateComponent(oldComponent: BlockComponent, newComponent: UnusedBlockComponent) {
        (components.findInHierarchy(oldComponent) as? BlockComponent)?.let { component ->
            component.name = newComponent.name
            component.time = newComponent.time
            component.description = newComponent.description
            component.colorOption = newComponent.colorOption
            component.file = newComponent.file

            component.tryRecalculateSize()
            uiState.value.centralComponent.tryRecalculateSize()
            updateCanvasCounter()
        }
    }

    private fun removeComponent(component: BlockComponent? = null) {
        val componentToRemove = component ?: (uiState.value.componentFocus as? ComponentFocus.Block)?.ref ?: return

        if (!retainProductOnBlock) {
            _uiState.update {
                it.copy(
                    unusedProducts = it.unusedProducts + componentToRemove.products
                )
            }
            componentToRemove.products.clear()
        }

        val unusedBlockComponent = componentToRemove.toUnusedBlockComponent(uiState.value.recipeId)

        viewModelScope.launch(Dispatchers.IO) {
            unusedBlocksDao.upsertUnusedBlock(unusedBlockComponent)
        }

        _uiState.update {
            it.copy(
                unusedBlockComponents = it.unusedBlockComponents + unusedBlockComponent
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
        clearComponentFocus()
    }

    private fun deleteBlock(component: BlockComponent) {
        val componentId = component.id
        removeComponent(component)

        val unusedComponent = uiState.value.unusedBlockComponents.find { it.id == componentId } ?: return
        deleteUnusedBlock(unusedComponent)
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

            val checkOffset = scaleAndTranslate(offset)
            val containedBlock = getBlockUnderPosition(checkOffset)

            containedBlock?.containsPointer(checkOffset, true)?.let { containment ->
                when (containment) {
                    BlockContainment.None -> Unit
                    BlockContainment.Whole -> {
                        _uiState.update {
                            it.copy(
                                componentFocus = ComponentFocus.Block(containedBlock),
                                canvasUiState = it.canvasUiState.copy(
                                    pointerMoveDeltaOffset = null,
                                    pointerMoveOffset = null
                                )
                            )
                        }

                        containedBlock.highlight()
                        return@launch
                    }
                    is BlockContainment.ProductLabel -> {
                        val product = containedBlock.products.removeAt(containment.index)

                        _uiState.update {
                            it.copy(
                                componentFocus = ComponentFocus.Product(product),
                                canvasUiState = it.canvasUiState.copy(
                                    pointerMoveDeltaOffset = null,
                                    pointerMoveOffset = null
                                )
                            )
                        }

                        uiState.value.centralComponent.tryRecalculateSize()

                        updateCanvasCounter()
                        return@launch
                    }
                    BlockContainment.FileIcon -> Unit
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
                val checkOffset = scaleAndTranslate(pointerDownOffset)
                val containerBlockComponent = components.getPointerContainer(
                    point = checkOffset,
                    onlyBlocks = true,
                    strict = true
                )

                if (containerBlockComponent != null && containerBlockComponent is BlockComponent) {
                    val containment = containerBlockComponent.containsPointer(checkOffset, true)
                    val file = containerBlockComponent.file

                    if (containment == BlockContainment.FileIcon && file != null) {
                        viewMediaFile?.invoke(containerBlockComponent.id, file)
                    } else {
                        setBlockEditorState(BlockEditorState.EditingBlock(containerBlockComponent))
                    }
                }

                pointerDownJob?.cancel()
                pointerDownJob = null
                return
            }
        }

        pointerDownJob?.cancel()
        pointerDownJob = null

        val componentFocus = uiState.value.componentFocus

        when {
            componentFocus is ComponentFocus.Block && uiState.value.centralComponent !is EmptyComponent -> {
                val pointerMoveOffset = uiState.value.canvasUiState.pointerMoveOffset ?: Offset.Zero
                val containerBlockComponent = components.getPointerContainer(scaleAndTranslate(pointerMoveOffset), true)

                if (containerBlockComponent != null && containerBlockComponent != componentFocus.ref) {
                    val relation = containerBlockComponent.definePointRelation(scaleAndTranslate(pointerMoveOffset))

                    componentFocus.ref.removeFromHierarchy { newComponent ->
                        _uiState.update {
                            it.copy(
                                centralComponent = newComponent
                            )
                        }
                    }

                    changeComponentPosition(containerBlockComponent, relation)
                }
            }
            componentFocus is ComponentFocus.Product -> {
                val movedProduct = componentFocus.weightedProduct
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
                } else {
                    uiState.value.centralComponent.tryRecalculateSize()
                }

                clearComponentFocus()
            }
        }

        components.doForEveryChild { (this as? BlockComponent)?.removeHighlight() }

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
        if (uiState.value.centralComponent is EmptyComponent) return
        val blockToInsert = (uiState.value.componentFocus as? ComponentFocus.Block)?.ref ?: return

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

    private fun getBlockUnderPosition(position: Offset? = null): BlockComponent? {
        val checkPos = position ?: scaleAndTranslate(uiState.value.canvasUiState.pointerMoveOffset  ?: return null)
        val containerBlockComponent = components.getPointerContainer(
            point = checkPos,
            onlyBlocks = true,
            strict = true
        )

        return containerBlockComponent as? BlockComponent
    }

    private fun processPointerMovement() {
        val pointerDownOffset = uiState.value.canvasUiState.pointerDownOffset ?: uiState.value.canvasUiState.center
        val pointerMoveOffset = uiState.value.canvasUiState.pointerMoveOffset ?: pointerDownOffset
        val pointerMoveDeltaOffset = uiState.value.canvasUiState.pointerMoveDeltaOffset

        val componentFocus = uiState.value.componentFocus

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
            componentFocus is ComponentFocus.Block -> {
                componentFocus.ref.removeHighlight()

                val containerBlockComponent = components.getPointerContainer(scaleAndTranslate(pointerMoveOffset), true)
                    ?: uiState.value.centralComponent

                if (containerBlockComponent == componentFocus.ref) {
                    if (containerBlockComponent.pointInBounds(scaleAndTranslate(pointerMoveOffset))) {
                        containerBlockComponent.highlightForNextFrame()
                    }
                } else {
                    val relation = containerBlockComponent.definePointRelation(scaleAndTranslate(pointerMoveOffset))

                    if (containerBlockComponent is BlockComponent) {
                        containerBlockComponent.shadeQuarterForNextFrame(relation)
                        containerBlockComponent.highlightForNextFrame()
                    }
                }

                updateCanvasCounter()
                return
            }
            uiState.value.draggingState == DraggingState.PRODUCT || componentFocus is ComponentFocus.Product -> {
                components.getPointerContainer(
                    point = scaleAndTranslate(pointerMoveOffset),
                    onlyBlocks = true,
                    strict = true
                )?.let { containerBlock ->
                    if (containerBlock.pointInBounds(scaleAndTranslate(pointerMoveOffset))) {
                        containerBlock.highlightForNextFrame()
                    }
                }

                updateCanvasCounter()
                return
            }
            else -> {
                if (pointerMoveDeltaOffset != Offset.Zero && pointerMoveDeltaOffset != null) {
                    val canvasOffset = uiState.value.canvasUiState.cachedOffset + (pointerMoveOffset - pointerDownOffset).div(uiState.value.canvasUiState.zoom)

                    updateCanvasOffset(canvasOffset)
                }
            }
        }

        updateCanvasCounter()
    }

    // Other

    private fun clearComponentFocus() {
        _uiState.update {
            it.copy(
                componentFocus = ComponentFocus.None
            )
        }
    }

    private fun scaleAndTranslate(position: Offset): Offset = uiState.value.canvasUiState.scaleAndTranslate(position)
}