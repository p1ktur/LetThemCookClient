package com.letthemcook.recipe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.core.domain.format.prettyString
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.recipe.domain.model.EditingError
import com.letthemcook.core.domain.model.status.LikeStatus
import com.letthemcook.recipe.R
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeUiAction
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeUiState
import com.letthemcook.recipe.domain.model.SaveStatus
import com.letthemcook.recipe.ui.components.EditingError
import com.letthemcook.recipe.ui.components.popups.WeightedProductsLabelContainer
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.IconButton
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.dialogs.AreYouSureDialog
import com.letthemcook.theme.components.dialogs.AreYouSureDialogConfig
import com.letthemcook.theme.components.images.RecipeImage
import com.letthemcook.theme.components.labels.EditedLabelContainer
import com.letthemcook.theme.components.textFields.MultiLineTextField
import com.letthemcook.theme.components.textFields.SingleLineTextField
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditedRecipeScreen(
    uiState: EditedRecipeUiState,
    onUiAction: (EditedRecipeUiAction) -> Unit
) {
    val wasPublished = remember { uiState.publicationDate != null }

    // Save Dialog
    val exitRecipeEditing = stringResource(R.string.exit_recipe_editing)
    val thereMayBeUnsavedChanges = stringResource(R.string.there_are_may_be_unsaved_changes_are_you_sure_you_want_to_exit)
    var areYouSureDialogConfig: AreYouSureDialogConfig? by remember { mutableStateOf(null) }
    val saveDialogConfigDefault = remember {
        AreYouSureDialogConfig(
            titleText = exitRecipeEditing,
            bodyText = thereMayBeUnsavedChanges,
            onOk = {},
            onDismiss = { areYouSureDialogConfig = null }
        )
    }

    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarBackClick {
                if (uiState.saveStatus == SaveStatus.NOT_SAVED) {
                    areYouSureDialogConfig = saveDialogConfigDefault.copy(
                        onOk = {
                            if (wasPublished) {
                                onUiAction(EditedRecipeUiAction.PopToProfile)
                            } else {
                                onUiAction(EditedRecipeUiAction.NavigateBack)
                            }
                            areYouSureDialogConfig = null
                        }
                    )
                } else {
                    onUiAction(EditedRecipeUiAction.NavigateBack)
                }
            }

            setShowNavigationBar(true)
            setOnNavigateToNewRecipe {
                if (uiState.saveStatus != SaveStatus.NOT_SAVED) {
                    areYouSureDialogConfig = saveDialogConfigDefault.copy(
                        onOk = {
                            if (!uiState.recipeIsNew) {
                                onUiAction(EditedRecipeUiAction.NavigateToNewRecipe)
                            }
                            areYouSureDialogConfig = null
                        }
                    )
                } else {
                    if (!uiState.recipeIsNew) {
                        onUiAction(EditedRecipeUiAction.NavigateToNewRecipe)
                    }
                }
            }
            setOnNavigateToProfile {
                if (uiState.saveStatus != SaveStatus.NOT_SAVED) {
                    areYouSureDialogConfig = saveDialogConfigDefault.copy(
                        onOk = {
                            onUiAction(EditedRecipeUiAction.NavigateToProfile)
                            areYouSureDialogConfig = null
                        }
                    )
                } else {
                    onUiAction(EditedRecipeUiAction.NavigateToProfile)
                }
            }
        }
    }

    val notSaved = stringResource(R.string.not_saved)
    val saving = stringResource(R.string.saving)
    val saved = stringResource(R.string.saved)

    LaunchedEffect(uiState.saveStatus) {
        screenContainer.setToolBarStatusText(
            value = when (uiState.saveStatus) {
                SaveStatus.NO_CHANGES -> null
                SaveStatus.NOT_SAVED -> notSaved
                SaveStatus.SAVING -> saving
                SaveStatus.SAVED -> saved
            }
        )
    }

    val coroutineScope = rememberCoroutineScope()

    // Media
    val mediaFilePicker = LocalScreenContainer.current.mediaFilePicker

    // Other
    val categoriesFilterNames = remember(uiState.categoriesFilter) { uiState.categoriesFilter.map { it.name } }
    val searchedCategoriesNames = remember(uiState.searchedCategories) { uiState.searchedCategories.map { it.name } }

    var editingError: EditingError? by remember { mutableStateOf(null) }
    val editingTooManyError: EditingError? by remember {
        derivedStateOf {
            when {
                uiState.categoriesFilter.size > 20 -> EditingError.TooManyCategories
                uiState.productsFilter.size > 20 -> EditingError.TooManyProducts
                else -> null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EditingError(editingError ?: editingTooManyError)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                RecipeImage(
                    modifier = Modifier.width(120.dp),
                    bitmap = uiState.recipeBitmap,
                    clipToRoundedRect = true,
                    onClick = { bitmap ->
                        onUiAction(EditedRecipeUiAction.ViewRecipeBitmap(bitmap))
                    }
                )
                IconButton(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.BottomEnd),
                    icon = Icons.Outlined.AddAPhoto,
                    onClick = {
                        val fileId = uiState.recipeBitmapId ?: UUID.randomUUID().toString()
                        mediaFilePicker.showDialog(FileType.IMAGE, fileId) {
                            (it as? MediaFile.Image)?.let { mediaFile ->
                                onUiAction(EditedRecipeUiAction.UpdateRecipeBitmap(fileId, mediaFile.bitmap))
                            }
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SingleLineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = uiState.name,
                    labelText = stringResource(R.string.name),
                    placeholderText = stringResource(R.string.type_name)
                )
                Text(
                    text = if (uiState.publicationDate == null) {
                        stringResource(R.string.archived)
                    } else {
                        stringResource(R.string.published) + uiState.publicationDate.prettyString()
                    },
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        modifier = Modifier.size(140.dp, 40.dp),
                        text = if (uiState.publicationDate != null) {
                            stringResource(R.string.unpublish)
                        } else {
                            stringResource(R.string.publish)
                        },
                        onClick = {
                            if (uiState.publicationDate != null) {
                                onUiAction(EditedRecipeUiAction.Archive)
                            } else {
                                if (uiState.name.text.isEmpty()) {
                                    editingError = EditingError.EmptyName
                                } else {
                                    editingError = null
                                    onUiAction(EditedRecipeUiAction.Publish)
                                }
                            }
                        }
                    )
                    if (uiState.saveStatus == SaveStatus.NOT_SAVED) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    onUiAction(EditedRecipeUiAction.SaveChanges)
                                },
                            imageVector = Icons.Outlined.Save,
                            contentDescription = "Save Icon",
                            tint = LocalAppTheme.current.text
                        )
                    }
                }
            }
        }
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = if (uiState.cookingTime != null) {
                stringResource(R.string.cooking_time) + uiState.cookingTime.toShortTimeString()
            } else {
                stringResource(R.string.no_cooking_yet)
            },
            style = LocalAppTheme.current.typography.bodyLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                modifier = Modifier.size(130.dp, 40.dp),
                text = stringResource(R.string.edit),
                onClick = {
                    onUiAction(EditedRecipeUiAction.EditCooking(uiState.recipeJson))
                }
            )
            if (uiState.recipeJson != null) {
                TextButton(
                    modifier = Modifier.size(130.dp, 40.dp),
                    text = stringResource(R.string.cook),
                    onClick = {
                        onUiAction(EditedRecipeUiAction.Cook(uiState.recipeJson))
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (uiState.publicationDate == null) {
                val deleteThisRecipe = stringResource(R.string.delete_this_recipe)
                val ifYouDeleteThisRecipe = stringResource(R.string.if_you_delete_this_recipe_all_your_work_will_be_gone_forever_are_you_sure_you_want_to_proceed)

                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            areYouSureDialogConfig = AreYouSureDialogConfig(
                                titleText = deleteThisRecipe,
                                bodyText = ifYouDeleteThisRecipe,
                                onOk = {
                                    areYouSureDialogConfig = null
                                    onUiAction(EditedRecipeUiAction.DeleteRecipe)
                                },
                                onDismiss = { areYouSureDialogConfig = null }
                            )
                        },
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Icon",
                    tint = LocalAppTheme.current.text
                )
            }
        }
        HorizontalDivider(color = LocalAppTheme.current.text)
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = uiState.dislikesAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = if (uiState.likeStatus == LikeStatus.DISLIKED) {
                    Icons.Filled.ThumbDown
                } else {
                    Icons.Outlined.ThumbDown
                },
                contentDescription = "Dislikes Icon",
                tint = LocalAppTheme.current.text
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = if (uiState.likeStatus == LikeStatus.LIKED) {
                    Icons.Filled.ThumbUp
                } else {
                    Icons.Outlined.ThumbUp
                },
                contentDescription = "Likes Icon",
                tint = LocalAppTheme.current.text
            )
            Text(
                text = uiState.likesAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Outlined.RemoveRedEye,
                contentDescription = "Views Icon",
                tint = LocalAppTheme.current.text
            )
            Text(
                text = uiState.viewsAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Outlined.SoupKitchen,
                contentDescription = "Preparations Icon",
                tint = LocalAppTheme.current.text
            )
            Text(
                text = uiState.preparationsAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = "Reviews Icon",
                tint = LocalAppTheme.current.text
            )
            Text(
                text = uiState.reviewsAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
        }
        MultiLineTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = uiState.description,
            labelText = stringResource(R.string.description),
            placeholderText = stringResource(R.string.type_description)
        )
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.attachments),
            style = LocalAppTheme.current.typography.bodyMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        mediaFilePicker.showDialog(
                            FileType.ANY,
                            UUID.randomUUID().toString()
                        ) {
                            onUiAction(EditedRecipeUiAction.AddFile(it.file))
                        }
                    }
                    .padding(4.dp),
                imageVector = Icons.Outlined.AttachFile,
                contentDescription = "Attach File Icon",
                tint = LocalAppTheme.current.text
            )
            uiState.attachments.forEachIndexed { index, file ->
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            onUiAction(EditedRecipeUiAction.SelectMediaFile(index))
                        }
                        .padding(4.dp),
                    imageVector = when (file.type) {
                        FileType.IMAGE -> Icons.Outlined.Image
                        FileType.VIDEO -> Icons.Outlined.VideoFile
                        FileType.ANY -> Icons.Outlined.FilePresent
                    },
                    contentDescription = "File Icon",
                    tint = LocalAppTheme.current.text
                )
            }
        }
        uiState.selectedAttachment?.let { attachment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = when (attachment.type) {
                        FileType.IMAGE -> stringResource(R.string.image)
                        FileType.VIDEO -> stringResource(R.string.video)
                        FileType.ANY -> stringResource(R.string.file)
                    },
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            onUiAction(EditedRecipeUiAction.ViewMediaFile(attachment))
                        }
                        .padding(4.dp),
                    imageVector = when (attachment.type) {
                        FileType.IMAGE -> Icons.Outlined.Image
                        FileType.VIDEO -> Icons.Outlined.VideoFile
                        FileType.ANY -> Icons.Outlined.FilePresent
                    },
                    contentDescription = "View File Icon",
                    tint = LocalAppTheme.current.text
                )
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val index = uiState.attachments.indexOf(attachment)

                            coroutineScope.launch(Dispatchers.IO) {
                                mediaFilePicker.deleteStoredFile(attachment) { wasDeleted ->
                                    if (wasDeleted) onUiAction(EditedRecipeUiAction.DeleteFile(index))
                                }
                            }
                        }
                        .padding(4.dp),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete File Icon",
                    tint = LocalAppTheme.current.text
                )
            }
        }
        HorizontalDivider(color = LocalAppTheme.current.text)
        EditedLabelContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            name = stringResource(R.string.categories),
            labels = categoriesFilterNames,
            searchTitle = stringResource(R.string.categories),
            searchText = uiState.categoriesSearchText,
            isLoading = uiState.loadingCategories,
            searchedLabels = searchedCategoriesNames,
            maxRows = 2,
            onContainerClick = {
                onUiAction(EditedRecipeUiAction.LoadCategories) },
            onSearchedLabelClick = { index ->
                onUiAction(EditedRecipeUiAction.AddCategory(index)) },
            onSearchedListEndReach = {
                onUiAction(EditedRecipeUiAction.LoadCategories) },
            onLabelClick = { index ->
                onUiAction(EditedRecipeUiAction.RemoveCategory(index))
            }
        )
        WeightedProductsLabelContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            name = stringResource(R.string.products),
            weightedProducts = uiState.productsFilter,
            searchTitle = stringResource(R.string.products),
            searchText = uiState.productsSearchText,
            isLoading = uiState.loadingProducts,
            searchedProducts = uiState.searchedProducts,
            maxRows = 2,
            onContainerClick = {
                onUiAction(EditedRecipeUiAction.LoadProducts) },
            onLabelCreate = { weightedProduct ->
                onUiAction(EditedRecipeUiAction.AddWeightedProduct(weightedProduct)) },
            onSearchedListEndReach = {
                onUiAction(EditedRecipeUiAction.LoadProducts) },
            onLabelClick = { index ->
                onUiAction(EditedRecipeUiAction.RemoveProduct(index))
            }
        )
        Spacer(modifier = Modifier.height(144.dp))
    }

    AreYouSureDialog(areYouSureDialogConfig)
}