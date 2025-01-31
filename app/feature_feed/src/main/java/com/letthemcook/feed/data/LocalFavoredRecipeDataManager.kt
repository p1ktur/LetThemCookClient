package com.letthemcook.feed.data

import com.letthemcook.feed.data.database.FavoredRecipeDao

class LocalFavoredRecipeDataManager(
    private val dao: FavoredRecipeDao
) {

//    suspend fun getBuiltRecipeAsComponent(recipeId: Int): Component {
//        val jsonString = dao.getRecipeById(recipeId).jsonString
//
//        // TODO get files
//
//        return RecipeGraphSerializer.deserializeComponent(jsonString)
//    }
//
//    suspend fun saveBuiltRecipeAsJson(recipeId: Int, component: Component) {
//        val jsonString = RecipeGraphSerializer.serializeToJson(component)
//
//        // TODO set files
//
//        dao.upsertRecipe(BuiltRecipe(recipeId, jsonString))
//    }
}