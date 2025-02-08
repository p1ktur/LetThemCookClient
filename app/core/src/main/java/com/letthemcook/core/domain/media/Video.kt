package com.letthemcook.core.domain.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun compressVideoWithSizeLimit(context: Context, videoUri: Uri, maxSizeMB: Int = 20): ByteArray? {
    var inputPath: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)

    context.contentResolver.query(videoUri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            inputPath = cursor.getString(columnIndex)
        }
    }

    if (inputPath == null) return null

    val outputFile = withContext(Dispatchers.IO) {
        File.createTempFile("COMPRESS_VIDEO_", ".mp4", context.cacheDir)
    }

    outputFile.deleteOnExit()

    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, videoUri)

    val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: return null
    retriever.release()

    val durationSec = durationMs / 1000.0
    val targetBitrate = ((maxSizeMB * 8 * 1024 * 1024) / durationSec).toInt()

    val command = "-i $inputPath -b:v ${targetBitrate}k -preset slow -c:a copy ${outputFile.absolutePath}"

    val session = FFmpegKit.execute(command)

    return if (ReturnCode.isSuccess(session.returnCode)) {
        val bytes = outputFile.readBytes()
        outputFile.delete()
        return bytes
    } else {
        null
    }
}