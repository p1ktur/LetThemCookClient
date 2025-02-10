package com.letthemcook.editor.ui.components.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.letthemcook.core.domain.format.toHoursString
import com.letthemcook.core.domain.format.toMinutesString
import com.letthemcook.core.domain.format.toSecondsString
import com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.media.MediaFilePicker
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.editor.R
import com.letthemcook.editor.domain.editor.color.ColorOption
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent
import com.letthemcook.editor.ui.components.colorChooser.ColorChooser
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.textFields.DigitsTextField
import com.letthemcook.theme.components.textFields.MultiLineTextField
import com.letthemcook.theme.components.textFields.SingleLineTextField
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

sealed interface BlockEditorState {
    data object Hidden : BlockEditorState
    data object Creating : BlockEditorState
    data class EditingUnusedBlock(val component: UnusedBlockComponent) : BlockEditorState
    data class EditingBlock(val component: BlockComponent) : BlockEditorState
}

private data class BlockEditorFields(
    val colorOption: ColorOption = ColorOption.WHITE,
    val nameText: String = "",
    val descriptionText: String = "",
    val timeHoursText: String = "",
    val timeMinutesText: String = "",
    val timeSecondsText: String = "",
    val blockFile: File? = null
)

@Composable
fun BlockEditorPopup(
    state: BlockEditorState,
    mediaFilePicker: MediaFilePicker,
    anchorPosition: Offset,
    anchorSize: IntSize,
    onEdit: (String, String, Int, Int, Int, ColorOption, File?) -> Unit,
    onViewMediaFile: (File) -> Unit,
    onRestoreState: (BlockEditorState) -> Unit,
    onDismiss: () -> Unit
) {
    val screenContainer = LocalScreenContainer.current

    var lastBlockEditorFields: BlockEditorFields? by remember { mutableStateOf(null) }
    var lastBlockEditorState by remember { mutableStateOf(state) }

    val coroutineScope = rememberCoroutineScope()
    val isMediaPickerDialogShown by remember { mediaFilePicker.isDialogShown }

    var showEmptyNameError by remember { mutableStateOf(false) }

    var colorOption by remember(state) {
        mutableStateOf(
            when (state) {
                BlockEditorState.Hidden -> ColorOption.WHITE
                BlockEditorState.Creating -> ColorOption.WHITE
                is BlockEditorState.EditingBlock -> state.component.colorOption
                is BlockEditorState.EditingUnusedBlock -> state.component.colorOption
            }
        )
    }

    val title = when (state) {
        BlockEditorState.Hidden -> ""
        BlockEditorState.Creating -> stringResource(R.string.create_new_block)
        is BlockEditorState.EditingBlock -> stringResource(R.string.edit_block)
        is BlockEditorState.EditingUnusedBlock -> stringResource(R.string.edit_block)
    }

    val buttonText = when (state) {
        BlockEditorState.Hidden -> ""
        BlockEditorState.Creating -> stringResource(R.string.create)
        is BlockEditorState.EditingBlock -> stringResource(R.string.edit)
        is BlockEditorState.EditingUnusedBlock -> stringResource(R.string.edit)
    }

    val nameText: TextFieldState = remember(state) {
        val text = when (state) {
            BlockEditorState.Hidden -> ""
            BlockEditorState.Creating -> ""
            is BlockEditorState.EditingBlock -> state.component.name
            is BlockEditorState.EditingUnusedBlock -> state.component.name
        }
        TextFieldState(text)
    }
    val descriptionText: TextFieldState = remember(state) {
        val text = when (state) {
            BlockEditorState.Hidden -> ""
            BlockEditorState.Creating -> ""
            is BlockEditorState.EditingBlock -> state.component.description
            is BlockEditorState.EditingUnusedBlock -> state.component.description
        }
        TextFieldState(text)
    }
    val timeHoursText: TextFieldState = remember(state) {
        val text = when (state) {
            BlockEditorState.Hidden -> ""
            BlockEditorState.Creating -> ""
            is BlockEditorState.EditingBlock -> state.component.time.toHoursString()
            is BlockEditorState.EditingUnusedBlock -> state.component.time.toHoursString()
        }
        TextFieldState(text)
    }
    val timeMinutesText: TextFieldState = remember(state) {
        val text = when (state) {
            BlockEditorState.Hidden -> ""
            BlockEditorState.Creating -> ""
            is BlockEditorState.EditingBlock -> state.component.time.toMinutesString()
            is BlockEditorState.EditingUnusedBlock -> state.component.time.toMinutesString()
        }
        TextFieldState(text)
    }
    val timeSecondsText: TextFieldState = remember(state) {
        val text = when (state) {
            BlockEditorState.Hidden -> ""
            BlockEditorState.Creating -> ""
            is BlockEditorState.EditingBlock -> state.component.time.toSecondsString()
            is BlockEditorState.EditingUnusedBlock -> state.component.time.toSecondsString()
        }
        TextFieldState(text)
    }

    var blockFile: File? by remember(state) {
        when (state) {
            BlockEditorState.Hidden -> mutableStateOf(null)
            BlockEditorState.Creating -> mutableStateOf(null)
            is BlockEditorState.EditingBlock -> mutableStateOf(state.component.file)
            is BlockEditorState.EditingUnusedBlock -> mutableStateOf(state.component.file)
        }
    }

    LaunchedEffect(screenContainer.viewingMedia) {
        if (!screenContainer.viewingMedia && lastBlockEditorState != BlockEditorState.Hidden) {
            onRestoreState(lastBlockEditorState)
        }
    }

    LaunchedEffect(state) {
        if (state != BlockEditorState.Hidden) lastBlockEditorFields?.let {

            colorOption = it.colorOption

            nameText.apply {
                clearText()
                edit { append(it.nameText) }
            }

            descriptionText.apply {
                clearText()
                edit { append(it.descriptionText) }
            }

            timeHoursText.apply {
                clearText()
                edit { append(it.timeHoursText) }
            }

            timeMinutesText.apply {
                clearText()
                edit { append(it.timeMinutesText) }
            }

            timeSecondsText.apply {
                clearText()
                edit { append(it.timeSecondsText) }
            }

            blockFile = it.blockFile

            lastBlockEditorFields = null
        }
    }

    if (state == BlockEditorState.Hidden || isMediaPickerDialogShown || screenContainer.viewingMedia) return

    val popupPositionProvider = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val delta = 48

                return if (windowSize.height - anchorBounds.height < popupContentSize.height) {
                    IntOffset(
                        x = 0,
                        y = anchorPosition.y.roundToInt() + anchorSize.height - delta
                    )
                } else {
                    IntOffset(
                        x = 0,
                        y = anchorPosition.y.roundToInt() - popupContentSize.height + delta
                    )
                }
            }
        }
    }

    Popup(
        popupPositionProvider = popupPositionProvider,
        properties = PopupProperties(focusable = true),
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LocalAppTheme.current.screenTwo, RoundedCornerShape(12.dp))
                .border(1.dp, LocalAppTheme.current.text, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = title,
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                Text(
                    text = colorOption.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            ColorChooser(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                selectedOption = colorOption,
                onOptionSelect = { colorOption = it }
            )
            if (showEmptyNameError) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error Show Icon",
                        tint = LocalAppTheme.current.errorText
                    )
                    Text(
                        text = stringResource(R.string.block_name_cannot_be_empty),
                        style = LocalAppTheme.current.typography.bodySmall,
                        color = LocalAppTheme.current.errorText
                    )
                }
            }
            SingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                state = nameText,
                labelText = stringResource(R.string.name),
                backgroundColor = LocalAppTheme.current.screenTwo
            )
            MultiLineTextField(
                modifier = Modifier.fillMaxWidth(),
                state = descriptionText,
                labelText = stringResource(R.string.description),
                backgroundColor = LocalAppTheme.current.screenTwo
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DigitsTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp),
                    state = timeHoursText,
                    labelText = stringResource(R.string.hours),
                    backgroundColor = LocalAppTheme.current.screenTwo
                )
                DigitsTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    state = timeMinutesText,
                    labelText = stringResource(R.string.minutes),
                    backgroundColor = LocalAppTheme.current.screenTwo
                )
                DigitsTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp),
                    state = timeSecondsText,
                    labelText = stringResource(R.string.seconds),
                    backgroundColor = LocalAppTheme.current.screenTwo
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (blockFile != null) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val blockId =
                                    (state as? BlockEditorState.EditingBlock)?.component?.id
                                        ?: (state as? BlockEditorState.EditingUnusedBlock)?.component?.id
                                        ?: return@clickable

                                blockFile = null

                                coroutineScope.launch(Dispatchers.IO) {
                                    mediaFilePicker.deleteStoredFile(blockId)
                                }
                            }
                            .padding(4.dp),
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete File Icon",
                        tint = LocalAppTheme.current.text
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val blockId = (state as? BlockEditorState.EditingBlock)?.component?.id
                                ?: (state as? BlockEditorState.EditingUnusedBlock)?.component?.id
                                ?: UUID.randomUUID().toString()

                            if (blockFile == null) {
                                mediaFilePicker.showDialog(FileType.ANY, blockId) {
                                    blockFile = it.file
                                }
                            } else blockFile?.let {
                                lastBlockEditorFields = BlockEditorFields(
                                    colorOption = colorOption,
                                    nameText = nameText.text.toString(),
                                    descriptionText = descriptionText.text.toString(),
                                    timeHoursText = timeHoursText.text.toString(),
                                    timeMinutesText = timeMinutesText.text.toString(),
                                    timeSecondsText = timeSecondsText.text.toString(),
                                    blockFile = blockFile
                                )
                                lastBlockEditorState = state

                                onDismiss()
                                onViewMediaFile(it)
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (blockFile?.type) {
                            FileType.IMAGE -> stringResource(R.string.image)
                            FileType.VIDEO -> stringResource(R.string.video)
                            FileType.ANY -> stringResource(R.string.file)
                            null -> stringResource(R.string.attach_file)
                        },
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = when (blockFile?.type) {
                            FileType.IMAGE -> Icons.Outlined.Image
                            FileType.VIDEO -> Icons.Outlined.VideoFile
                            FileType.ANY -> Icons.Outlined.FilePresent
                            null -> Icons.Outlined.AttachFile
                        },
                        contentDescription = "File Icon",
                        tint = LocalAppTheme.current.text
                    )
                }
                if (blockFile != null) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val blockId =
                                    (state as? BlockEditorState.EditingBlock)?.component?.id
                                        ?: (state as? BlockEditorState.EditingUnusedBlock)?.component?.id
                                        ?: return@clickable

                                mediaFilePicker.showDialog(FileType.ANY, blockId) {
                                    blockFile = it.file
                                }
                            }
                            .padding(4.dp),
                        imageVector = Icons.Outlined.AttachFile,
                        contentDescription = "Attach File Icon",
                        tint = LocalAppTheme.current.text
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    text = buttonText,
                    onClick = {
                        if (nameText.text.isEmpty()) {
                            showEmptyNameError = true
                            return@TextButton
                        } else {
                            showEmptyNameError = false
                        }

                        try {
                            onEdit(
                                nameText.text.toString(),
                                descriptionText.text.toString(),
                                timeHoursText.text.toString().toIntOrNull() ?: 0,
                                timeMinutesText.text.toString().toIntOrNull() ?: 0,
                                timeSecondsText.text.toString().toIntOrNull() ?: 0,
                                colorOption,
                                blockFile
                            )

                            nameText.clearText()
                            descriptionText.clearText()
                            timeHoursText.clearText()
                            timeMinutesText.clearText()
                            timeSecondsText.clearText()
                        } catch (_: Exception) {
                            onDismiss()
                        }
                    }
                )
            }
        }
    }
}