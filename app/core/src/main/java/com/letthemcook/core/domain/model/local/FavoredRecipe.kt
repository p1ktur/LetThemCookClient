package com.letthemcook.core.domain.model.local

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.items.RecipeItemData
import java.time.LocalDateTime

@Entity
data class FavoredRecipe(
    @PrimaryKey(autoGenerate = false) var id: String = "",
    var name: String = "",
    var authorLogin: String = "",
    var likesAmount: Int = 0,
    var dislikesAmount: Int = 0,
    var reviewsAmount: Int = 0,
    var preparationsAmount: Int = 0,
    var description: String = "",
    var imageFile: File? = null,
    var publicationDate: LocalDateTime = LocalDateTime.now(),
    @Ignore val bitmap: Bitmap? = null
) {
    fun asItemData(bitmap: Bitmap?): RecipeItemData {
        return RecipeItemData(
            id = id,
            name = name,
            authorLogin = authorLogin,
            likesAmount = likesAmount,
            dislikesAmount = dislikesAmount,
            reviewsAmount = reviewsAmount,
            preparationsAmount = preparationsAmount,
            description = description,
            bitmap = bitmap,
            publicationDate = publicationDate
        )
    }
}
