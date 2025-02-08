package com.letthemcook.editor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.letthemcook.core.domain.dataConvertion.typeConvertion.FileConverter
import com.letthemcook.core.domain.dataConvertion.typeConvertion.WeightedProductListConverter
import com.letthemcook.editor.domain.editor.components.block.UnusedBlockComponent

@Database(
    entities = [UnusedBlockComponent::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(value = [
    FileConverter::class,
    WeightedProductListConverter::class
])
abstract class UnusedBlocksDatabase : RoomDatabase() {

    abstract fun getDao(): UnusedBlocksDao

    companion object {
        fun getInstance(context: Context): UnusedBlocksDatabase {
            return Room.databaseBuilder(context, UnusedBlocksDatabase::class.java, "UnusedBlocksDatabase").build()
        }
    }
}