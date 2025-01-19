package com.letthemcook.core.domain.media.camera

import android.Manifest
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.IOException

class MediaFilePickerManager(
    private val context: Context,
    private val name: String
) {

    private lateinit var cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    private lateinit var mediaPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>

    private val imageUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val imageBitmap: MutableStateFlow<ImageBitmap?> = MutableStateFlow(
        run {
            retrieveImageFile()
            uriToImageBitmap()
        }
    )

    @Composable
    fun RegisterLaunchers() {
        val cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isNewImageCaptured ->
            if (isNewImageCaptured) imageBitmap.value = uriToImageBitmap()
        }

        cameraPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isCameraAllowed ->
            if (isCameraAllowed) {
                try {
                    retrieveImageFile()
                    cameraLauncher.launch(imageUri.value)
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mediaPickerLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                imageUri.value = it
                imageBitmap.value = uriToImageBitmap()
            }
        }
    }

    fun launchGallery() {
        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        mediaPickerLauncher.launch(request)
    }

    fun launchCamera() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun uriToImageBitmap(): ImageBitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri.value)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri.value)
            }.run {
                Bitmap.createScaledBitmap(this, 320, 320, true)
            }.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    @Throws(IOException::class)
    private fun retrieveImageFile(): File {
        val profileImageFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$name.jpg")
        imageUri.value = FileProvider.getUriForFile(context, "${context.packageName}.provider", profileImageFile)

        return profileImageFile
    }
}