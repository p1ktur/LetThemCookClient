package com.letthemcook.core.domain.model.auth

import android.graphics.Bitmap
import com.letthemcook.core.domain.dataConvertion.serialization.LocalDateTimeSerializer
import com.letthemcook.core.domain.model.items.UserItemData
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Serializable
data class User(
    val id: String,
    val login: String,
    val email: String,
    val phone: String?,
    val name: String?,
    val surname: String?,
    val about: String?,
    @Serializable(with = LocalDateTimeSerializer::class) val birthDate: LocalDateTime?,
    val profileBitmapId: String?,
    val averageRating: Float = 0f,
    val totalRecipes: Int = 0,
    val totalPreparations: Int = 0,
    val totalFollowers: Int = 0,
    var isFollowed: Boolean = false
) {
    fun birthDateString(): String? {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.valueOf("yyyy.MM.dd"))

        return birthDate?.format(formatter)
    }

    fun asItemData(bitmap: Bitmap?): UserItemData {
        return UserItemData(
            id = id,
            login = login,
            name = name,
            surname = surname,
            totalFollowers = totalFollowers,
            profileBitmapId = profileBitmapId,
            bitmap = bitmap
        )
    }
}