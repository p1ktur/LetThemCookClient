package com.letthemcook.feed.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.letthemcook.core.domain.dataConvertion.typeConvertion.FileConverter
import com.letthemcook.core.domain.dataConvertion.typeConvertion.UriConverter
import com.letthemcook.feed.domain.model.data.RecipeItemData

@Database(
    entities = [RecipeItemData::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(
    value = [FileConverter::class]
)
abstract class FavoredRecipeDatabase : RoomDatabase() {

    abstract fun getDao(): FavoredRecipeDao

    companion object {
        fun getInstance(context: Context): FavoredRecipeDatabase {
            return Room.databaseBuilder(context, FavoredRecipeDatabase::class.java, "FavoredRecipeDatabase").build()
        }
    }
}