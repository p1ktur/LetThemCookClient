package com.letthemcook.core.domain.model.file

fun getRecipePictureFileName(recipeId: String): String = "RecipePicture$recipeId"
fun getRecipeFilesPrefix(recipeId: String): String = "RecipeFile${recipeId}_"