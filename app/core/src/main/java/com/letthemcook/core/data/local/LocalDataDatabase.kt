package com.letthemcook.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.letthemcook.core.domain.dataConvertion.typeConvertion.FileConverter
import com.letthemcook.core.domain.model.local.RecipeJson
import com.letthemcook.core.domain.model.local.FavoredRecipe
import com.letthemcook.core.domain.model.local.reactions.RecipeReaction
import com.letthemcook.core.domain.model.local.reactions.ReviewLike

@Database(
    entities = [FavoredRecipe::class, RecipeJson::class, RecipeReaction::class, ReviewLike::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(value = [FileConverter::class])
abstract class LocalDataDatabase : RoomDatabase() {

    abstract fun getFavoredRecipeDao(): FavoredRecipeDao
    abstract fun getRecipeJsonDao(): RecipeJsonDao
    abstract fun getRecipeReactionDao(): RecipeReactionDao
    abstract fun getReviewLikeDao(): ReviewLikeDao
    abstract fun getFollowingDao(): FollowingDao

    companion object {
        fun getInstance(context: Context): LocalDataDatabase {
            return Room.databaseBuilder(context, LocalDataDatabase::class.java, "LocalDataDatabase").build()
        }
    }
}