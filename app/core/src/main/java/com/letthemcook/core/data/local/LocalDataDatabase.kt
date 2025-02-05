package com.letthemcook.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.letthemcook.core.domain.dataConvertion.typeConvertion.CategoryListConverter
import com.letthemcook.core.domain.dataConvertion.typeConvertion.FileConverter
import com.letthemcook.core.domain.dataConvertion.typeConvertion.LocalDateTimeConverter
import com.letthemcook.core.domain.dataConvertion.typeConvertion.StringListConverter
import com.letthemcook.core.domain.dataConvertion.typeConvertion.WeightedProductListConverter
import com.letthemcook.core.domain.model.recipe.reactions.RecipeReaction
import com.letthemcook.core.domain.model.recipe.reactions.ReviewLike
import com.letthemcook.core.domain.model.recipe.Recipe

@Database(
    entities = [Recipe::class, RecipeReaction::class, ReviewLike::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(value = [
    FileConverter::class,
    LocalDateTimeConverter::class,
    StringListConverter::class,
    WeightedProductListConverter::class,
    CategoryListConverter::class
])
abstract class LocalDataDatabase : RoomDatabase() {

    abstract fun getRecipeDao(): RecipeDao
    abstract fun getRecipeReactionDao(): RecipeReactionDao
    abstract fun getReviewLikeDao(): ReviewLikeDao

    companion object {
        fun getInstance(context: Context): LocalDataDatabase {
            return Room.databaseBuilder(context, LocalDataDatabase::class.java, "LocalDataDatabase").build()
        }
    }
}