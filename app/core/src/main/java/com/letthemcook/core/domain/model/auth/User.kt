package com.letthemcook.core.domain.model.auth

import com.letthemcook.core.domain.dataConvertion.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
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
    @Serializable(with = LocalDateSerializer::class) val birthDate: LocalDate?,
    val profilePictureId: String?,
    val averageRating: Float = 0f,
    val totalRecipes: Int = 0,
    val totalPreparations: Int = 0,
    val totalFollowers: Int = 0
) {
    fun birthDateString(): String? {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.valueOf("yyyy.MM.dd"))

        return birthDate?.format(formatter)
    }
}