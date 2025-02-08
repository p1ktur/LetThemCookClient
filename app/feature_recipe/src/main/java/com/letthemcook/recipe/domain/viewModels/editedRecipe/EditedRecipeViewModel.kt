package com.letthemcook.recipe.domain.viewModels.editedRecipe

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.domain.list.filterOn
import com.letthemcook.core.domain.media.compressBitmap
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.remote.WeightedProduct
import com.letthemcook.recipe.domain.model.LikeStatus
import com.letthemcook.recipe.domain.model.SaveStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

@OptIn(FlowPreview::class)
class EditedRecipeViewModel(
    recipeId: String?,
    authManager: AuthManager,
    private val recipeManager: RecipeManager,
    private val remoteFileManager: RemoteFileManager,
    private val localFileManager: LocalFileManager,
    private val localDataManager: LocalDataManager
) : ViewModel() {

    private val _uiState = run {
        val user = authManager.getUser()

        MutableStateFlow(EditedRecipeUiState(
            recipeIsNew = recipeId == null,
            recipeId = recipeId ?: UUID.randomUUID().toString(),
            ownerId = user?.id ?: "",
            authorLogin = user?.login ?: ""
        ))
    }
    val uiState = _uiState.asStateFlow()

    private var lastCategoryPage = 0
    private var allCategoryPagesReached = false
    private var lastCategorySearchText = ""

    private var lastProductPage = 0
    private var allProductPagesReached = false
    private var lastProductSearchText = ""

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (recipeId != null) {
                var recipe = localDataManager.getRecipe(recipeId)

                if (recipe == null) {
                    val remoteRecipe = recipeManager.getRecipe(recipeId)

                    recipe = remoteRecipe
                } else if (recipe.publicationDate != null) {
                    recipe.updateUserInteractionsData(recipeManager)
                }

                if (recipe == null) return@launch

                val bitmap = recipe.getBitmap(localFileManager, remoteFileManager)

                val isLiked = when (recipe.getLikeStatus(localDataManager)) {
                    null -> LikeStatus.NONE
                    true -> LikeStatus.LIKED
                    false -> LikeStatus.DISLIKED
                }

                val attachments = recipe.getAttachmentsAndSave(localFileManager, remoteFileManager)

                _uiState.update {
                    it.fromRecipe(
                        recipe = recipe,
                        bitmap = bitmap,
                        isLiked = isLiked,
                        attachments = attachments
                    )
                }
            }
        }

        viewModelScope.launch {
            snapshotFlow { uiState.value.name.text }
                .drop(if (recipeId == null) 1 else 2)
                .collectLatest {
                    _uiState.update {
                        it.copy(
                            saveStatus = SaveStatus.NOT_SAVED
                        )
                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { uiState.value.description.text }
                .drop(if (recipeId == null) 1 else 2)
                .collectLatest {
                    _uiState.update {
                        it.copy(
                            saveStatus = SaveStatus.NOT_SAVED
                        )
                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { uiState.value.categoriesSearchText.text }
                .debounce(500)
                .collectLatest { text ->
                    withContext(Dispatchers.IO) {
                        searchCategories(text.toString())
                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { uiState.value.productsSearchText.text }
                .debounce(500)
                .collectLatest { text ->
                    withContext(Dispatchers.IO) {
                        searchProducts(text.toString())
                    }
                }
        }
    }

    fun onUiAction(action: EditedRecipeUiAction) {
        when (action) {
            EditedRecipeUiAction.NavigateBack -> saveChanges(true)
            EditedRecipeUiAction.PopToProfile -> saveChanges(true)
            EditedRecipeUiAction.NavigateToHome -> Unit
            EditedRecipeUiAction.NavigateToNewRecipe -> Unit
            EditedRecipeUiAction.NavigateToProfile -> Unit
            is EditedRecipeUiAction.NavigateToOtherProfile -> Unit

            EditedRecipeUiAction.SaveChanges -> saveChanges()
            is EditedRecipeUiAction.UpdateRecipeJson -> updateRecipeJson(action.recipeJson, action.cookingTime)

            is EditedRecipeUiAction.SelectMediaFile -> selectFile(action.index)
            is EditedRecipeUiAction.ViewMediaFile -> Unit
            is EditedRecipeUiAction.AddFile -> addFile(action.file)
            is EditedRecipeUiAction.DeleteFile -> deleteFile(action.index)

            is EditedRecipeUiAction.ViewRecipeBitmap -> Unit
            is EditedRecipeUiAction.UpdateRecipeBitmap -> updateRecipeBitmap(action.fileId, action.bitmap)

            EditedRecipeUiAction.LoadCategories -> viewModelScope.launch(Dispatchers.IO) { searchCategories() }
            is EditedRecipeUiAction.AddCategory -> addCategory(action.index)
            is EditedRecipeUiAction.RemoveCategory -> removeCategory(action.index)

            EditedRecipeUiAction.LoadProducts -> viewModelScope.launch(Dispatchers.IO) { searchProducts() }
            is EditedRecipeUiAction.AddWeightedProduct -> addWeightedProduct(action.weightedProduct)
            is EditedRecipeUiAction.RemoveProduct -> removeWeightedProduct(action.index)

            EditedRecipeUiAction.Publish -> publish()
            EditedRecipeUiAction.Archive -> archive()
            is EditedRecipeUiAction.EditCooking -> Unit
            is EditedRecipeUiAction.Cook -> Unit
        }
    }

    private fun saveChanges(forced: Boolean = false) {
        if (uiState.value.saveStatus != SaveStatus.NOT_SAVED && !forced) return

        _uiState.update {
            it.copy(
                saveStatus = SaveStatus.SAVING,
                categoriesFilter = it.categoriesFilter.take(20),
                productsFilter = it.productsFilter.take(20)
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val recipe = uiState.value.toRecipe()
            val attachments = uiState.value.attachments

            localDataManager.saveRecipe(recipe)

            if (recipe.publicationDate != null) {
                if (recipeManager.publishRecipe(recipe)) {
                    attachments.forEach { file ->
                        localFileManager.getFileBytes(file)?.let { bytes ->
                            val params = RemoteFileManager.RequestParams(
                                userId = recipe.ownerId,
                                fileId = file.uid,
                                recipeId = recipe.id,
                                type = file.type,
                                isAttachment = true
                            )

                            remoteFileManager.uploadFile(params, bytes)
                        }
                    }
                }
            }

            _uiState.update {
                it.copy(
                    saveStatus = SaveStatus.SAVED
                )
            }
        }
    }

    private fun updateRecipeJson(recipeJson: String?, cookingTime: Long?) {
        _uiState.update {
            it.copy(
                saveStatus = SaveStatus.NOT_SAVED,
                recipeJson = recipeJson,
                cookingTime = cookingTime
            )
        }

        saveChanges()
    }

    private fun selectFile(index: Int) {
        try {
            _uiState.update {
                it.copy(
                    selectedAttachment = if (it.attachments[index] != it.selectedAttachment) {
                        it.attachments[index]
                    } else {
                        null
                    }
                )
            }
        } catch (_: Exception) {}
    }

    private fun addFile(file: File) {
        _uiState.update {
            it.copy(
                saveStatus = SaveStatus.NOT_SAVED,
                attachments = it.attachments + file
            )
        }
    }

    private fun deleteFile(index: Int) {
        try {
            val deletedFile = uiState.value.attachments[index]

            _uiState.update {
                it.copy(
                    saveStatus = SaveStatus.NOT_SAVED,
                    selectedAttachment = null,
                    attachments = it.attachments - deletedFile
                )
            }

            viewModelScope.launch(Dispatchers.IO) {
                val params = RemoteFileManager.RequestParams(
                    userId = uiState.value.ownerId,
                    fileId = deletedFile.uid,
                    recipeId = uiState.value.recipeId,
                    type = deletedFile.type,
                    isAttachment = true
                )

                remoteFileManager.deleteFile(params)
            }
        } catch (_: Exception) {}
    }

    private fun updateRecipeBitmap(fileId: String, bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                saveStatus = SaveStatus.NOT_SAVED,
                recipeBitmapId = fileId,
                recipeBitmap = bitmap
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val params = RemoteFileManager.RequestParams(
                userId = uiState.value.ownerId,
                fileId = fileId,
                recipeId = uiState.value.recipeId,
                type = FileType.IMAGE
            )

            remoteFileManager.uploadFile(params, bitmap.compressBitmap())
        }
    }

    private suspend fun searchCategories(searchText: String? = null) {
        _uiState.update {
            it.copy(
                loadingCategories = true
            )
        }

        val text = searchText ?: uiState.value.categoriesSearchText.text.toString()

        val isFilteringOn = if (lastCategorySearchText != text) {
            lastCategorySearchText = text
            lastCategoryPage = 0
            allCategoryPagesReached = false
            true
        } else {
            if (allCategoryPagesReached) return
            false
        }

        val perPage = 20
        val categories = recipeManager.searchCategories(
            page = lastCategoryPage++,
            perPage = perPage,
            searchText = text
        ).filterNot { uiState.value.categoriesFilter.contains(it) }

        if (categories.size < perPage) allCategoryPagesReached = true

        if (categories.isEmpty()) {
            allCategoryPagesReached = true

            _uiState.update {
                it.copy(
                    searchedCategories = emptyList(),
                    loadingCategories = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    searchedCategories = if (isFilteringOn) {
                        it.searchedCategories.filterOn(categories)
                    } else {
                        it.searchedCategories + categories
                    },
                    loadingCategories = false
                )
            }
        }
    }

    private fun addCategory(index: Int) {
        try {
            if (uiState.value.categoriesFilter.contains(uiState.value.searchedCategories[index])) return

            _uiState.update {
                it.copy(
                    saveStatus = SaveStatus.NOT_SAVED,
                    categoriesFilter = it.categoriesFilter + it.searchedCategories[index],
                    searchedCategories = it.searchedCategories - it.searchedCategories[index]
                )
            }
        } catch (_: Exception) {}
    }

    private fun removeCategory(index: Int) {
        try {
            val removedCategory = uiState.value.categoriesFilter[index]

            _uiState.update {
                it.copy(
                    saveStatus = SaveStatus.NOT_SAVED,
                    categoriesFilter = it.categoriesFilter - removedCategory,
                    searchedCategories = if (removedCategory.name.contains(uiState.value.categoriesSearchText.text.toString())) {
                        it.searchedCategories + removedCategory
                    } else {
                        it.searchedCategories
                    }
                )
            }
        } catch (_: Exception) {}
    }

    private suspend fun searchProducts(searchText: String? = null) {
        _uiState.update {
            it.copy(
                loadingProducts = true
            )
        }

        val text = searchText ?: uiState.value.productsSearchText.text.toString()

        val isFilteringOn = if (lastProductSearchText != text) {
            lastProductSearchText = text
            lastProductPage = 0
            allProductPagesReached = false
            true
        } else {
            if (allProductPagesReached) return
            false
        }

        val productsFilterData = uiState.value.productsFilter.map { it.data }

        val perPage = 20
        val products = recipeManager.searchProducts(
            page = lastProductPage++,
            perPage = perPage,
            searchText = text
        ).filterNot { productsFilterData.contains(it) }

        if (products.size < perPage) allProductPagesReached = true

        if (products.isEmpty()) {
            _uiState.update {
                it.copy(
                    searchedProducts = emptyList(),
                    loadingProducts = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    searchedProducts = if (isFilteringOn) {
                        it.searchedProducts.filterOn(products)
                    } else {
                        it.searchedProducts + products
                    },
                    loadingProducts = false
                )
            }
        }
    }

    private fun addWeightedProduct(weightedProduct: WeightedProduct) {
        try {
            val products = uiState.value.productsFilter.map { it.data }
            if (products.contains(weightedProduct.data)) return

            _uiState.update {
                it.copy(
                    saveStatus = SaveStatus.NOT_SAVED,
                    productsFilter = it.productsFilter + weightedProduct,
                    searchedProducts = it.searchedProducts - weightedProduct.data
                )
            }
        } catch (_: Exception) {}
    }

    private fun removeWeightedProduct(index: Int) {
        try {
            val removedProduct = uiState.value.productsFilter[index]

            _uiState.update {
                it.copy(
                    saveStatus = SaveStatus.NOT_SAVED,
                    productsFilter = it.productsFilter - removedProduct,
                    searchedProducts = if (removedProduct.data.name.contains(uiState.value.productsSearchText.text.toString())) {
                        it.searchedProducts + removedProduct.data
                    } else {
                        it.searchedProducts
                    }
                )
            }
        } catch (_: Exception) {}
    }

    private fun publish() {
        saveChanges()

        viewModelScope.launch(Dispatchers.IO) {
            val recipe = uiState.value.toRecipe()
            val attachments = uiState.value.attachments

            recipe.publicationDate = LocalDateTime.now()

            if (recipeManager.publishRecipe(recipe)) {
                localDataManager.saveRecipe(recipe)

                attachments.forEach { file ->
                    localFileManager.getFileBytes(file)?.let { bytes ->
                        val params = RemoteFileManager.RequestParams(
                            userId = recipe.ownerId,
                            fileId = file.uid,
                            recipeId = recipe.id,
                            type = file.type,
                            isAttachment = true
                        )

                        remoteFileManager.uploadFile(params, bytes)
                    }
                }
                _uiState.update {
                    it.copy(
                        saveStatus = SaveStatus.SAVED,
                        publicationDate = recipe.publicationDate
                    )
                }
            }
        }
    }

    private fun archive() {
        viewModelScope.launch(Dispatchers.IO) {
            val recipe = uiState.value.toRecipe()
            recipe.publicationDate = null

            if (recipeManager.unpublishRecipe(uiState.value.recipeId)) {
                localDataManager.saveRecipe(recipe)
                _uiState.update {
                    it.copy(
                        publicationDate = null
                    )
                }
            }
        }
    }
}