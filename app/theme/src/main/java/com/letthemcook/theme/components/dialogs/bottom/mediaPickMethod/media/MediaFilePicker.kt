package com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.MediaFile
import java.util.UUID

interface MediaFilePicker {
    val isDialogShown: MutableState<Boolean>
    val currentFileType: MutableState<FileType>

    @Composable
    fun RegisterLaunchers()

    fun showDialog(
        fileType: FileType,
        fileId: String = UUID.randomUUID().toString(),
        onReceiveMediaFile: (MediaFile) -> Unit
    )
    fun hideDialog()

    fun launchGallery(fileType: FileType)
    fun launchCamera(fileType: FileType)

    suspend fun getStoredFile(
        fileId: String,
        onReceiveMediaFile: (MediaFile) -> Unit
    ): Boolean

    suspend fun deleteStoredFile(fileId: String, onFileDeleted: ((Boolean) -> Unit)? = null)
    suspend fun deleteStoredFile(file: File, onFileDeleted: ((Boolean) -> Unit)? = null)
}