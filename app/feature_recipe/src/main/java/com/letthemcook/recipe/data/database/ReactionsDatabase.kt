package com.letthemcook.recipe.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.letthemcook.recipe.domain.model.data.recipe.RecipeReaction
import com.letthemcook.recipe.domain.model.data.review.ReviewLike

@Database(
    entities = [RecipeReaction::class, ReviewLike::class],
    exportSchema = false,
    version = 1
)
abstract class ReactionsDatabase : RoomDatabase() {

    abstract fun getDao(): ReactionsDao

    companion object {
        fun getInstance(context: Context): ReactionsDatabase {
            return Room.databaseBuilder(context, ReactionsDatabase::class.java, "LocalRecipeDatabase").build()
        }
    }
}