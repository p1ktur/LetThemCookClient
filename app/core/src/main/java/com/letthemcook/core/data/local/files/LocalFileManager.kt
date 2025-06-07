package com.letthemcook.core.data.local.files

import android.app.RecoverableSecurityException
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import java.util.UUID
import java.io.File as JFile

class LocalFileManager(
    private val context: Context,
    private val dao: FileDao
) {

    fun getFileBytes(file: File): ByteArray? {
        val resolver = context.contentResolver

        return try {
            resolver.openInputStream(file.uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getFileByUid(uid: String): File? {
        return dao.getFileByUid(uid)
    }

    suspend fun saveFile(
        bytes: ByteArray,
        type: FileType,
        uid: String = UUID.randomUUID().toString()
    ): File? {
        val resolver = context.contentResolver

        val contentValues = when (type) {
            FileType.IMAGE -> ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$uid.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, type.mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LetThemCook")
            }
            FileType.VIDEO -> ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$uid.mp4")
                put(MediaStore.Images.Media.MIME_TYPE, type.mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Movies/LetThemCook")
            }
            FileType.ANY -> return null
        }

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (type == FileType.IMAGE) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            }
        } else {
            if (type == FileType.IMAGE) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
        }

        val uri = resolver.insert(collectionUri, contentValues)

        return uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(bytes)
            }

            File(uid = uid, uri = it, type = type).apply {
                dao.upsertFile(this)
            }
        }
    }

    fun createTemporaryFile(uid: String, type: FileType, bytes: ByteArray): File {
        val cacheDir = context.cacheDir

        val jFile = JFile.createTempFile(uid, type.ext(), cacheDir)
        jFile.deleteOnExit()

        jFile.writeBytes(bytes)

        return File(
            uid = uid,
            type = type,
            uri = jFile.toUri()
        )
    }

    suspend fun updateFile(file: File, bytes: ByteArray): File {
        val resolver = context.contentResolver

        try {
            resolver.openOutputStream(file.uri)?.use { outputStream ->
                outputStream.write(bytes)
            }
        } catch (_: Exception) {
            saveFile(bytes, file.type, file.uid)
        }

        return file
    }

    suspend fun deleteFile(file: File): Boolean {
        val resolver = context.contentResolver

        return try {
            resolver.delete(file.uri, null, null) > 0
        } catch (e: SecurityException) {
            false
        }.apply {
            dao.deleteFile(file)
        }
    }

    suspend fun deleteFile(
        file: File,
        deleteFileLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ): Boolean {
        val resolver = context.contentResolver
        var shouldInvokeCallback: Boolean

        try {
            shouldInvokeCallback = resolver.delete(file.uri, null, null) > 0
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException = e as? RecoverableSecurityException
                recoverableSecurityException?.userAction?.actionIntent?.intentSender?.let { intentSender ->
                    val senderRequest = IntentSenderRequest.Builder(intentSender).build()
                    deleteFileLauncher.launch(senderRequest)
                }
            }

            shouldInvokeCallback = false
        }

        dao.deleteFile(file)

        return shouldInvokeCallback
    }
}