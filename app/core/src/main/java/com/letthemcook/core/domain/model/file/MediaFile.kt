package com.letthemcook.core.domain.model.file

import android.graphics.Bitmap

sealed interface MediaFile {

    val file: File

    data class Image(val bitmap: Bitmap, override val file: File) : MediaFile
    data class Video(override val file: File) : MediaFile
}