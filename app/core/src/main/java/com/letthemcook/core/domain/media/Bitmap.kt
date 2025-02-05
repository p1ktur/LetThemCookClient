package com.letthemcook.core.domain.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Bitmap.toBytes(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

    return outputStream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size)
}

fun Bitmap.compressBitmap(maxSizeInMB: Int = 10): ByteArray {
    if (byteCount < maxSizeInMB * 1024 * 1024) return toBytes()

    var quality = 100
    val outputStream = ByteArrayOutputStream()

    do {
        outputStream.reset()
        compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        quality -= 5
    } while (outputStream.size() > maxSizeInMB * 1024 * 1024)

    return outputStream.toByteArray()
}