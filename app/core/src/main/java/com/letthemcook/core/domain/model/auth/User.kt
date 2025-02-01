package com.letthemcook.core.domain.model.auth

import com.letthemcook.core.domain.dataConvertion.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class User(
    val id: String,
    val login: String,
    val email: String,
    val phone: String?,
    val about: String?,
    @Serializable(with = LocalDateSerializer::class) val birthDate: LocalDate?,
    val profilePictureId: String?,
    val totalRecipes: Int = 0,
    val totalPreparations: Int = 0,
    val totalFollowers: Int = 0
)