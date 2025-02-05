package com.letthemcook.core.domain.model.items

import android.graphics.Bitmap

data class UserItemData(
    val id: String,
    val login: String,
    val name: String?,
    val surname: String?,
    val totalFollowers: Int,
    val profileBitmapId: String?,
    val bitmap: Bitmap? = null
)