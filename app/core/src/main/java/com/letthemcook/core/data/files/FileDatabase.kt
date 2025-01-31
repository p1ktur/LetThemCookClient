package com.letthemcook.core.data.files

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.letthemcook.core.domain.dataConvertion.typeConvertion.UriConverter
import com.letthemcook.core.domain.model.file.File

@Database(
    entities = [File::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(
    value = [UriConverter::class]
)
abstract class FileDatabase : RoomDatabase() {

    abstract fun getDao(): FileDao

    companion object {
        fun getInstance(context: Context): FileDatabase {
            return Room.databaseBuilder(context, FileDatabase::class.java, "FileDatabase").build()
        }
    }
}