package com.letthemcook.theme.ui.navigation

import android.graphics.Bitmap
import com.letthemcook.core.domain.model.file.File

data class MediaViewerAccess(
    val stopViewing: () -> Unit,
    val viewFile: (File) -> Unit,
    val viewBitmap: (Bitmap) -> Unit
) {
    companion object {
        val Dummy = MediaViewerAccess({}, {}, {})
    }
}
