package com.letthemcook.core.domain.media

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.MediaFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class MediaFilePickerManager(
    private val context: Context,
    private val localFileManager: LocalFileManager
) {
    // UI
    val isDialogShown = mutableStateOf(false)

    // Common
    private lateinit var cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    private lateinit var mediaPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>

    private var currentFileName: String = ""

    private var _currentFileType = FileType.IMAGE
    var currentFileType = mutableStateOf(FileType.IMAGE)

    private var onReceiveMediaFile: ((MediaFile) -> Unit)? = null

    // Camera
    private var temporaryCameraFileUri: Uri = Uri.EMPTY

    // UI
    @Composable
    fun RegisterLaunchers() {
        val coroutineScope = rememberCoroutineScope()

        // Camera
        val cameraImageLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isNewImageCaptured ->
            if (!isNewImageCaptured) return@rememberLauncherForActivityResult

            // TODO for video also

            coroutineScope.launch(Dispatchers.IO) {
                processImageUri(temporaryCameraFileUri)
            }
        }

        val cameraVideoLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CaptureVideo()
        ) { isNewImageCaptured ->
            if (!isNewImageCaptured) return@rememberLauncherForActivityResult

            coroutineScope.launch(Dispatchers.IO) {
                processVideoUri(temporaryCameraFileUri)
            }
        }

        cameraPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isCameraAllowed ->
            if (isCameraAllowed) {
                try {
                    when (_currentFileType) {
                        FileType.IMAGE -> {
                            val temporaryFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temporary.jpg")
                            temporaryCameraFileUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", temporaryFile)

                            cameraImageLauncher.launch(temporaryCameraFileUri)
                        }
                        FileType.VIDEO -> {
                            val temporaryFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "temporary.mp4")
                            temporaryCameraFileUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", temporaryFile)

                            cameraVideoLauncher.launch(temporaryCameraFileUri)
                        }
                        FileType.ANY -> return@rememberLauncherForActivityResult
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                onReceiveMediaFile = null
            }
        }

        // Media
        val mediaPickerLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            when (_currentFileType) {
                FileType.IMAGE ->  coroutineScope.launch(Dispatchers.IO) {
                    processImageUri(uri)
                }
                FileType.VIDEO ->  coroutineScope.launch(Dispatchers.IO) {
                    processVideoUri(uri)
                }
                FileType.ANY -> return@rememberLauncherForActivityResult
            }
        }

        mediaPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val isMediaPickerAllowed = results.values.all { it }

            if (isMediaPickerAllowed) {
                try {
                    val request = when (_currentFileType) {
                        FileType.IMAGE -> PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        FileType.VIDEO -> PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        FileType.ANY -> return@rememberLauncherForActivityResult
                    }
                    mediaPickerLauncher.launch(request)
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                onReceiveMediaFile = null
            }
        }
    }

    fun showDialog(
        fileType: FileType,
        fileId: String = UUID.randomUUID().toString(),
        onReceiveMediaFile: (MediaFile) -> Unit
    ) {
        isDialogShown.value = true

        currentFileName = fileId
        _currentFileType = fileType
        currentFileType.value = fileType
        this.onReceiveMediaFile = onReceiveMediaFile
    }

    fun hideDialog() {
        isDialogShown.value = false
    }

    fun launchGallery(fileType: FileType) {
        _currentFileType = fileType

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            mediaPermissionLauncher.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mediaPermissionLauncher.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            mediaPermissionLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }

    fun launchCamera(fileType: FileType) {
        _currentFileType = fileType

        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Files
    suspend fun getStoredFile(
        fileId: String,
        onReceiveMediaFile: (MediaFile) -> Unit
    ): Boolean {
        localFileManager.getFileByUid(fileId)?.let { file ->
            when (file.type) {
                FileType.IMAGE -> {
                    val bitmap = localFileManager.getFileAsBitmap(file) ?: return false
                    val mediaFile = MediaFile.Image(bitmap, file)
                    onReceiveMediaFile(mediaFile)
                }
                FileType.VIDEO -> {
                    val mediaFile = MediaFile.Video(file)
                    onReceiveMediaFile(mediaFile)
                }
                FileType.ANY -> Unit
            }
        } ?: return false

        return true
    }

    suspend fun getStoredFilesIndexed(
        fileId: String,
        startIndex: Int,
        onReceiveMediaFile: (MediaFile) -> Unit
    ) {
        var searching = true
        var currentIndex = startIndex

        while (searching) {
            searching = getStoredFile(fileId + currentIndex, onReceiveMediaFile)
            currentIndex++
        }
    }

    suspend fun deleteStoredFile(fileId: String): Boolean {
        localFileManager.getFileByUid(fileId)?.let { file ->
            localFileManager.deleteFile(file)
        } ?: return false

        return true
    }

    // Private
    private suspend fun processImageUri(uri: Uri) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val bitmapBytes = bitmap.compressBitmap()

            val existingFile = localFileManager.getFileByUid(currentFileName)
            existingFile?.type = _currentFileType

            val file = if (existingFile == null) {
                localFileManager.saveFile(bitmapBytes, _currentFileType, currentFileName)
            } else {
                localFileManager.updateFile(existingFile, bitmapBytes)
            } ?: return

            val mediaFile = MediaFile.Image(bitmap, file)

            onReceiveMediaFile?.invoke(mediaFile)
            onReceiveMediaFile = null
        } finally { }
    }

    private suspend fun processVideoUri(uri: Uri) {
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            } ?: return

            val existingFile = localFileManager.getFileByUid(currentFileName)
            existingFile?.type = _currentFileType

            val file = if (existingFile == null) {
                localFileManager.saveFile(bytes, _currentFileType, currentFileName)
            } else {
                localFileManager.updateFile(existingFile, bytes)
            } ?: return

            val mediaFile = MediaFile.Video(file)

            onReceiveMediaFile?.invoke(mediaFile)
            onReceiveMediaFile = null
        } finally { }
    }
}