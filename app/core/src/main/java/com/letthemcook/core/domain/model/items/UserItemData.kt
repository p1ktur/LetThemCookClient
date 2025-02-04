package com.letthemcook.core.domain.model.items

import android.graphics.Bitmap
import com.letthemcook.core.domain.dataConvertion.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class UserItemData(
    val id: String,
    val login: String,
    val name: String?,
    val surname: String?,
    val totalFollowers: Int,
    val profilePictureId: String?,
    val bitmap: Bitmap? = null
)