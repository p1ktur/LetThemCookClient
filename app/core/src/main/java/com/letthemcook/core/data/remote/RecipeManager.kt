package com.letthemcook.core.data.remote

import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.domain.http.delete
import com.letthemcook.core.domain.http.get
import com.letthemcook.core.domain.http.post
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.extensions.toBitmap
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.core.domain.model.remote.Category
import com.letthemcook.core.domain.model.remote.Product
import com.letthemcook.core.domain.model.remote.Recipe
import com.letthemcook.core.domain.model.remote.reactions.toLikeStatus
import io.ktor.client.call.body
import io.ktor.util.StringValues
import java.net.URLEncoder

class RecipeManager(
    private val authManager: AuthManager,
    private val remoteFileManager: RemoteFileManager,
    private val localDataManager: LocalDataManager
) {

    // Recipes
    suspend fun getFeedRecipes(page: Int? = null, perPage: Int = 10): List<RecipeItemData> {
        if (!authManager.checkAccessTokenAndTryRefresh()) return emptyList()

        return get(
            urlString = "/recipes",
            params = StringValues.build {
                append("page", page.toString())
                append("perPage", perPage.toString())
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { response ->
                val recipes = response.body<List<Recipe>>()

                recipes.map { recipe ->
                    val params = RemoteFileManager.RequestParams(
                        userId = recipe.ownerId,
                        fileId = recipe.bitmapId.toString(),
                        recipeId = recipe.id,
                        type = FileType.IMAGE
                    )
                    val recipeImage = remoteFileManager.getFile(params)

                    val likeStatus = localDataManager.getRecipeReaction(recipe.id).toLikeStatus()

                    recipe.asItemData(recipeImage?.toBitmap(), likeStatus)
                }
            },
            onError = { emptyList() }
        )
    }

    suspend fun searchRecipes(
        page: Int? = null,
        perPage: Int = 10,
        searchText: String,
        searchType: String,
        sortType: String,
        categories: List<String>,
        products: List<String>
    ): List<RecipeItemData> {
        if (!authManager.checkAccessTokenAndTryRefresh()) return emptyList()

        return get(
            urlString = "/recipes",
            params = StringValues.build {
                append("page", page.toString())
                append("perPage", perPage.toString())
                append("searchText", URLEncoder.encode(searchText, "utf-8"))
                append("searchType", searchType)
                append("sortType", sortType)
                categories.forEach { category -> append("category", URLEncoder.encode(category, "utf-8")) }
                products.forEach { product -> append("product", URLEncoder.encode(product, "utf-8")) }
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { response ->
                val recipes = response.body<List<Recipe>>()

                recipes.map { recipe ->
                    val params = RemoteFileManager.RequestParams(
                        userId = recipe.ownerId,
                        fileId = recipe.bitmapId.toString(),
                        recipeId = recipe.id,
                        type = FileType.IMAGE
                    )
                    val recipeImage = remoteFileManager.getFile(params)

                    val likeStatus = localDataManager.getRecipeReaction(recipe.id).toLikeStatus()

                    recipe.asItemData(recipeImage?.toBitmap(), likeStatus)
                }
            },
            onError = { emptyList() }
        )
    }

    suspend fun getUserRecipes(userId: String, page: Int = 0, perPage: Int = 10): List<RecipeItemData> {
        if (!authManager.checkAccessTokenAndTryRefresh()) return emptyList()

        return get(
            urlString = "/recipes",
            params = StringValues.build {
                append("page", page.toString())
                append("perPage", perPage.toString())
                append("userId", userId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { response ->
                val recipes = response.body<List<Recipe>>()

                recipes.map { recipe ->
                    val params = RemoteFileManager.RequestParams(
                        userId = recipe.ownerId,
                        fileId = recipe.bitmapId.toString(),
                        recipeId = recipe.id,
                        type = FileType.IMAGE
                    )
                    val recipeImage = remoteFileManager.getFile(params)

                    val likeStatus = localDataManager.getRecipeReaction(recipe.id).toLikeStatus()

                    recipe.asItemData(recipeImage?.toBitmap(), likeStatus)
                }
            },
            onError = { emptyList() }
        )
    }

    suspend fun getRecipe(recipeId: String, language: String): Recipe? {
        if (!authManager.checkAccessTokenAndTryRefresh()) return null

        return get(
            urlString = "/recipe",
            params = StringValues.build {
                append("recipeId", recipeId)
                append("language", language)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { it.body<Recipe>() },
            onError = { null }
        )
    }

    suspend fun publishRecipe(recipe: Recipe): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return post(
            urlString = "/recipe",
            body = recipe,
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun unpublishRecipe(recipeId: String): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return delete(
            urlString = "/recipe",
            params = StringValues.build {
                append("recipeId", recipeId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun viewRecipe(recipeId: String): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return post(
            urlString = "/recipe_view",
            params = StringValues.build {
                append("recipeId", recipeId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun prepareRecipe(recipeId: String): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return post(
            urlString = "/recipe_prepare",
            params = StringValues.build {
                append("recipeId", recipeId)
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    suspend fun reactOnRecipe(recipeId: String, wasLiked: Boolean?, liked: Boolean?): Boolean {
        if (!authManager.checkAccessTokenAndTryRefresh()) return false

        return post(
            urlString = "/recipe_react",
            params = StringValues.build {
                append("recipeId", recipeId)
                append("wasLiked", wasLiked.toString())
                append("liked", liked.toString())
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { true },
            onError = { false }
        )
    }

    //Secondary
    suspend fun searchCategories(
        page: Int? = null,
        perPage: Int = 10,
        language: String,
        searchText: String,
    ): List<Category> {
        if (!authManager.checkAccessTokenAndTryRefresh()) return emptyList()

        return get(
            urlString = "/categories",
            params = StringValues.build {
                append("page", page.toString())
                append("perPage", perPage.toString())
                append("language", language)
                append("searchText", URLEncoder.encode(searchText, "utf-8"))
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { it.body<List<Category>>() },
            onError = { emptyList() }
        )
    }

    suspend fun searchProducts(
        page: Int? = null,
        perPage: Int = 10,
        language: String,
        searchText: String,
    ): List<Product> {
        if (!authManager.checkAccessTokenAndTryRefresh()) return emptyList()

        return get(
            urlString = "/products",
            params = StringValues.build {
                append("page", page.toString())
                append("perPage", perPage.toString())
                append("language", language)
                append("searchText", URLEncoder.encode(searchText, "utf-8"))
            },
            headers = StringValues.build {
                append("Authorization", "Bearer ${authManager.getAccessToken()}")
            },
            onResponse = { it.body<List<Product>>() },
            onError = { emptyList() }
        )
    }
}