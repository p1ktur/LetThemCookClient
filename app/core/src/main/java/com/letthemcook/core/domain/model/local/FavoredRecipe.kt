package com.letthemcook.core.domain.model.local

import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.letthemcook.core.domain.model.file.File

@Entity
data class FavoredRecipe(
    @PrimaryKey(autoGenerate = false) var id: String,
    var name: String = "",
    var authorLogin: String = "",
    var likesAmount: Int = 0,
    var dislikesAmount: Int = 0,
    var reviewsAmount: Int = 0,
    var preparationsAmount: Int = 0,
    var description: String = "",
    var imageFile: File? = null,
    @Ignore val imageBitmap: ImageBitmap? = null
)
