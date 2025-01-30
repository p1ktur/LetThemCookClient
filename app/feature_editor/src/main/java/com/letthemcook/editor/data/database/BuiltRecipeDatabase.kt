package com.letthemcook.editor.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.letthemcook.editor.domain.model.data.BuiltRecipe

@Database(
    entities = [BuiltRecipe::class],
    exportSchema = false,
    version = 1
)
abstract class BuiltRecipeDatabase : RoomDatabase() {

    abstract fun getDao(): BuiltRecipeDao

    companion object {
        fun getInstance(context: Context): BuiltRecipeDatabase {
            return Room.databaseBuilder(context, BuiltRecipeDatabase::class.java, "BuiltRecipeDatabase").build()
        }
    }
}