package com.letthemcook.core.data.files

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.file.File

@Dao
interface FileDao {

    @Query("SELECT * FROM File WHERE name LIKE :name LIMIT 1")
    suspend fun getFileByName(name: String): File?

    @Upsert
    suspend fun upsertFile(file: File)

    @Delete
    suspend fun deleteFile(file: File)
}