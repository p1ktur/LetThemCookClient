package com.letthemcook.core.data.local.files

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import com.letthemcook.core.domain.media.toBitmap
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import java.util.UUID

class LocalFileManager(
    private val context: Context,
    private val dao: FileDao
) {

    fun getFileAsBitmap(file: File): Bitmap? {
        if (file.type != FileType.IMAGE) return null

        val resolver = context.contentResolver

        var bytes: ByteArray? = null

        resolver.openInputStream(file.uri)?.use { inputStream ->
            bytes = inputStream.readBytes()
        }

        return bytes?.toBitmap()
    }

    suspend fun getFileByUid(uid: String): File? {
        return dao.getFileByUid(uid)
    }

    suspend fun saveFile(
        bytes: ByteArray,
        type: FileType,
        name: String = UUID.randomUUID().toString()
    ): File? {
        val resolver = context.contentResolver

        val contentValues = when (type) {
            FileType.IMAGE -> ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, type.mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LetThemCook")
            }
            FileType.VIDEO -> ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$name.mp4")
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

            File(uid = name, uri = it, type = type).apply {
                dao.upsertFile(this)
            }
        }
    }

    fun updateFile(file: File, bytes: ByteArray): File {
        val resolver = context.contentResolver

        resolver.openOutputStream(file.uri)?.use { outputStream ->
            outputStream.write(bytes)
        }

        return file
    }

    suspend fun deleteFile(file: File) {
        val resolver = context.contentResolver

        resolver.delete(file.uri, null, null)

        dao.deleteFile(file)
    }
}