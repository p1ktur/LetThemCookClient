package com.letthemcook.core.domain.model.data.file

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.letthemcook.core.domain.dataConvertion.serialization.UriSerializer
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class File(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    @Serializable(with = UriSerializer::class) var uri: Uri,
    var type: FileType
)
