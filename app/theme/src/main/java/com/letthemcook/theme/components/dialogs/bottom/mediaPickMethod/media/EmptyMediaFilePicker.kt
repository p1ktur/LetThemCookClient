package com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.MediaFile

object EmptyMediaFilePicker : MediaFilePicker {
    override val isDialogShown: MutableState<Boolean> = mutableStateOf(false)
    override val currentFileType: MutableState<FileType> = mutableStateOf(FileType.IMAGE)

    @Composable
    override fun RegisterLaunchers() = Unit

    override fun showDialog(
        fileType: FileType,
        fileId: String,
        onReceiveMediaFile: (MediaFile) -> Unit
    ) = Unit
    override fun hideDialog() = Unit

    override fun launchGallery(fileType: FileType) = Unit
    override fun launchCamera(fileType: FileType) = Unit

    override suspend fun getStoredFile(
        fileId: String,
        onReceiveMediaFile: (MediaFile) -> Unit
    ): Boolean = false

    override suspend fun deleteStoredFile(fileId: String, onFileDeleted: ((Boolean) -> Unit)?) = Unit
    override suspend fun deleteStoredFile(file: File, onFileDeleted: ((Boolean) -> Unit)?) = Unit
}