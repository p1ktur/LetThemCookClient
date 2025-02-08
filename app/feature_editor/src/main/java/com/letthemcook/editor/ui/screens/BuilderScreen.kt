package com.letthemcook.editor.ui.screens

import android.content.ClipData
import android.content.ClipDescription
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.getLongTime
import com.letthemcook.core.domain.media.MediaFilePickerManager
import com.letthemcook.editor.domain.dragging.CanvasDragAndDropManager
import com.letthemcook.editor.domain.dragging.DraggingState
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent
import com.letthemcook.editor.domain.editor.components.prototype.countBlocks
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiAction
import com.letthemcook.editor.domain.viewModels.builder.BuilderUiState
import com.letthemcook.editor.ui.components.BlockItem
import com.letthemcook.editor.ui.components.canvas.RecipeCanvas
import com.letthemcook.editor.ui.components.popups.BlockEditorPopup
import com.letthemcook.editor.ui.components.popups.BlockEditorState
import com.letthemcook.editor.ui.modifier.rowScrollbar
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.IconButton
import com.letthemcook.theme.components.dialogs.MediaPickMethodDialog
import com.letthemcook.theme.components.labels.LabelIcon
import com.letthemcook.theme.components.labels.LabelItem
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BuilderScreen(
    uiState: BuilderUiState,
    onUiAction: (BuilderUiAction) -> Any?
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setToolBarStatusText(uiState.recipeName)
            setOnToolBarBackClick { onUiAction(BuilderUiAction.NavigateBack) }

            setShowNavigationBar(false)
        }
    }

    val mediaFilePickerManager = koinInject<MediaFilePickerManager>()
    mediaFilePickerManager.RegisterLaunchers()

    var isBlocksMenuVisible by remember { mutableStateOf(true) }

    var containerPosition by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var canvasGlobalPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val textMeasurer = rememberTextMeasurer()
    val blockComponentTitleTextStyle = MaterialTheme.typography.titleLarge
    val blockComponentNameTextStyle = MaterialTheme.typography.bodyLarge
    val blockComponentContentTextStyle = MaterialTheme.typography.bodyMedium

    val canvasDragAndDropManager = remember(uiState.unusedProducts, uiState.unusedBlockComponents, canvasGlobalPosition) {
        CanvasDragAndDropManager(
            canvasGlobalPosition = canvasGlobalPosition,
            unusedProducts = uiState.unusedProducts,
            unusedBlockComponents = uiState.unusedBlockComponents,
            textMeasurer = textMeasurer,
            nameTextStyle = blockComponentNameTextStyle,
            contentTextStyle = blockComponentContentTextStyle,
            onUiAction = onUiAction
        )
    }

    val productsRowScrollState = rememberScrollState()
    val blocksRowScrollState = rememberScrollState()

    val canAddBlocks by remember {
        derivedStateOf {
            uiState.centralComponent.countBlocks() + uiState.unusedBlockComponents.size < 30
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalAppTheme.current.background)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isBlocksMenuVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (uiState.unusedProducts.isEmpty()) {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = "All products are used",
                            style = LocalAppTheme.current.typography.bodyLarge
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = "Use all products",
                            style = LocalAppTheme.current.typography.bodyMedium
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable {
                                isBlocksMenuVisible = false
                            }
                            .padding(2.dp),
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = "Expand Button",
                        tint = LocalAppTheme.current.text
                    )
                }
                if (uiState.unusedProducts.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .rowScrollbar(productsRowScrollState)
                            .horizontalScroll(productsRowScrollState)
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        uiState.unusedProducts.forEach { product ->
                            key(product.data.id) {
                                LabelItem(
                                    modifier = Modifier.dragAndDropSource {
                                        detectTapGestures(onLongPress = {
                                            onUiAction(BuilderUiAction.SetDraggingState(DraggingState.PRODUCT))
                                            startTransfer(
                                                DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText("Product", product.data.id.toString()),
                                                    flags = View.DRAG_FLAG_GLOBAL
                                                )
                                            )
                                        })
                                    },
                                    text = product.toString(),
                                    icon = LabelIcon.NONE
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                HorizontalDivider(color = LocalAppTheme.current.text)
                Row(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .rowScrollbar(blocksRowScrollState)
                        .horizontalScroll(blocksRowScrollState)
                        .onGloballyPositioned {
                            containerPosition = it.positionInRoot()
                        }
                        .onSizeChanged {
                            containerSize = it
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canAddBlocks) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            modifier = Modifier.size(40.dp),
                            icon = Icons.Outlined.Add,
                            containerColor = LocalAppTheme.current.screenOne,
                            onClick = {
                                onUiAction(BuilderUiAction.SetBlockEditorState(BlockEditorState.Creating))
                            },
                            isOutlined = true
                        )
                    }
                    if (uiState.unusedBlockComponents.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier,
                            text = "All blocks are used",
                            style = LocalAppTheme.current.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                        uiState.unusedBlockComponents.forEach { block ->
                            key(block.hashCode()) {
                                BlockItem(
                                    modifier = Modifier
                                        .dragAndDropSource {
                                            detectTapGestures(
                                                onTap = {
                                                    onUiAction(BuilderUiAction.SetBlockEditorState(BlockEditorState.EditingUnusedBlock(block)))
                                                },
                                                onLongPress = {
                                                    onUiAction(BuilderUiAction.SetDraggingState(DraggingState.BLOCK))
                                                    startTransfer(
                                                        DragAndDropTransferData(
                                                            clipData = ClipData.newPlainText("Block", block.hashCode().toString()),
                                                            flags = View.DRAG_FLAG_GLOBAL
                                                        )
                                                    )
                                                }
                                            )
                                        },
                                    name = block.name,
                                    time = block.time,
                                    description = block.description,
                                    colorOption = block.colorOption
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isBlocksMenuVisible = true
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Blocks menu",
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Icon(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .padding(2.dp),
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expand Button",
                        tint = LocalAppTheme.current.text
                    )
                }
            }
        }
        HorizontalDivider(color = LocalAppTheme.current.text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            RecipeCanvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RectangleShape)
                    .dragAndDropTarget(
                        shouldStartDragAndDrop = { event ->
                            event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        },
                        target = canvasDragAndDropManager
                    )
                    .onGloballyPositioned {
                        canvasGlobalPosition = it.positionInWindow()
                    }
                    .onSizeChanged {
                        canvasSize = it
                    },
                uiState = uiState,
                textMeasurer = textMeasurer,
                blockComponentTitleTextStyle = blockComponentTitleTextStyle,
                blockComponentNameTextStyle = blockComponentNameTextStyle,
                blockComponentContentTextStyle = blockComponentContentTextStyle,
                onUiAction = onUiAction
            )
            IconButton(
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .align(Alignment.TopStart),
                icon = Icons.Outlined.SoupKitchen,
                containerColor = LocalAppTheme.current.screenOne,
                onClick = {
                    onUiAction(BuilderUiAction.TryDemoCooking)
                },
                isOutlined = true
            )
        }
    }

    BlockEditorPopup(
        state = uiState.blockEditorState,
        mediaFilePickerManager = mediaFilePickerManager,
        anchorPosition = containerPosition,
        anchorSize = containerSize,
        onEdit = { name, description, hours, minutes, seconds, colorOption, file ->
            val newBlockComponent = UnusedBlockComponent(
                recipeId = uiState.recipeId,
                name = name,
                description = description,
                time = getLongTime(hours, minutes, seconds),
                colorOption = colorOption,
                file = file
            )

            when (uiState.blockEditorState) {
                BlockEditorState.Hidden -> Unit
                BlockEditorState.Creating -> {
                    onUiAction(BuilderUiAction.AddUnusedComponent(newBlockComponent))
                }
                is BlockEditorState.EditingUnusedBlock -> {
                    onUiAction(BuilderUiAction.UpdateUnusedComponent(uiState.blockEditorState.component, newBlockComponent))
                }
                is BlockEditorState.EditingBlock -> {
                    onUiAction(BuilderUiAction.UpdateComponent(uiState.blockEditorState.component, newBlockComponent))
                }
            }

            onUiAction(BuilderUiAction.SetBlockEditorState(BlockEditorState.Hidden))
        },
        onViewMediaFile = { file ->
            val blockId = when (val state = uiState.blockEditorState) {
                BlockEditorState.Creating -> ""
                is BlockEditorState.EditingBlock -> state.component.id
                is BlockEditorState.EditingUnusedBlock -> state.component.id
                BlockEditorState.Hidden -> ""
            }

            onUiAction(BuilderUiAction.ViewMediaFile(blockId, file))
        },
        onRestoreState = {
            onUiAction(BuilderUiAction.SetBlockEditorState(it))
        },
        onDismiss = {
            onUiAction(BuilderUiAction.SetBlockEditorState(BlockEditorState.Hidden))
        }
    )

    MediaPickMethodDialog(mediaFilePickerManager)
}