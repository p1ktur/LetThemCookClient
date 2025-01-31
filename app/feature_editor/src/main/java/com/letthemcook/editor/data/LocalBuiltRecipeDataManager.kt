package com.letthemcook.editor.data

import com.letthemcook.editor.data.database.BuiltRecipeDao
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.model.data.BuiltRecipe
import com.letthemcook.editor.domain.serialization.RecipeGraphSerializer

class LocalBuiltRecipeDataManager(
    private val dao: BuiltRecipeDao,
    private val recipeGraphSerializer: RecipeGraphSerializer
) {

    suspend fun getBuiltRecipeAsComponent(recipeId: Int): Component {
        val jsonString = dao.getRecipeById(recipeId).jsonString

        // TODO get files

        return recipeGraphSerializer.deserializeComponent(jsonString)
    }

    suspend fun saveBuiltRecipeAsJson(recipeId: Int, component: Component) {
        val jsonString = recipeGraphSerializer.serializeToJson(component)

        // TODO set files

        dao.upsertRecipe(BuiltRecipe(recipeId, jsonString))
    }
}