package com.letthemcook.core.domain.model.file

fun getProfilePictureFileName(): String = "ProfilePicture"
fun getRecipePictureFileName(recipeId: Int): String = "RecipePicture$recipeId"
fun getRecipeFilesPrefix(recipeId: Int): String = "RecipeFile${recipeId}_"