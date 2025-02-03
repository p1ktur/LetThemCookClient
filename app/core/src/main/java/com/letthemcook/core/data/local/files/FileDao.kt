package com.letthemcook.core.data.local.files

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.file.File

@Dao
interface FileDao {

    @Query("SELECT * FROM File WHERE uid LIKE :uid LIMIT 1")
    suspend fun getFileByUid(uid: String): File?

    @Upsert
    suspend fun upsertFile(file: File)

    @Delete
    suspend fun deleteFile(file: File)
}