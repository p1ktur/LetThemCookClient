package com.letthemcook.recipe.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.media.MediaFilePickerManager
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.file.MediaFile
import com.letthemcook.core.domain.model.file.getRecipeFilesPrefix
import com.letthemcook.core.domain.model.file.getRecipePictureFileName
import com.letthemcook.recipe.domain.viewModels.reviews.ReviewsUiState
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeUiAction
import com.letthemcook.recipe.domain.viewModels.editedRecipe.EditedRecipeUiState
import com.letthemcook.recipe.ui.components.ReviewItem
import com.letthemcook.recipe.ui.components.dialogs.ReviewTextFieldDialog
import com.letthemcook.recipe.ui.components.popups.WeightedProductsLabelContainer
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.dialogs.MediaPickMethodDialog
import com.letthemcook.theme.components.labels.EditedLabelContainer
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import com.letthemcook.theme.components.textFields.BorderlessTextField
import com.letthemcook.theme.components.textFields.MultiLineTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun EditedRecipeScreen(
    uiState: EditedRecipeUiState,
    reviewsUiState: ReviewsUiState,
    onUiAction: (EditedRecipeUiAction) -> Unit
) {
    // Media
    val recipePictureName = getRecipePictureFileName(uiState.id)
    val recipeFileName = getRecipeFilesPrefix(uiState.id)
    var recipePictureBitmap: Bitmap? by remember { mutableStateOf(null) }

    val mediaFilePickerManager = koinInject<MediaFilePickerManager>()
    mediaFilePickerManager.RegisterLaunchers()

    // Reviews
    var isReviewTextFieldDialogShown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            mediaFilePickerManager.getStoredFile(recipePictureName) {
                recipePictureBitmap = (it as? MediaFile.Image)?.bitmap
            }

            mediaFilePickerManager.getStoredFilesIndexed(recipeFileName, 0) {
                onUiAction(EditedRecipeUiAction.AddFile(it.file))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer()
        ToolBar(
            modifier = Modifier.fillMaxWidth(),
            onBackClick = {
                onUiAction(EditedRecipeUiAction.NavigateBack)
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()), // TODO nested scroll!!
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f / 4f)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = recipePictureBitmap?.asImageBitmap()
                        ?: ImageBitmap.imageResource(id = com.letthemcook.theme.R.drawable.image_placeholder),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.FillBounds
                )
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (uiState.isPublished) {
                                onUiAction(EditedRecipeUiAction.Archive)
                            } else {
                                onUiAction(EditedRecipeUiAction.Publish)
                            }
                        }
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    imageVector = if (uiState.isPublished) {
                        Icons.Default.Archive
                    } else {
                        Icons.Default.Publish
                    },
                    contentDescription = "Publish Button",
                    tint = LocalAppTheme.current.text
                )
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = uiState.authorLogin,
                        style = LocalAppTheme.current.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BorderlessTextField(
                            modifier = Modifier.weight(1f),
                            state = uiState.name,
                            placeholderText = "Type name"
                        )
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    mediaFilePickerManager.showDialog(FileType.IMAGE, recipePictureName) {
                                        recipePictureBitmap = (it as? MediaFile.Image)?.bitmap
                                    }
                                }
                                .padding(8.dp),
                            imageVector = Icons.Outlined.AddAPhoto,
                            contentDescription = "Camera Button",
                            tint = LocalAppTheme.current.text
                        )
                    }
                }
                TextButton(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopCenter),
                    text = "Edit recipe",
                    onClick = {
                        onUiAction(EditedRecipeUiAction.StartEditing)
                    }
                )
            }
            HorizontalDivider(color = LocalAppTheme.current.text)
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = uiState.dislikesAmount.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Outlined.ThumbDown,
                    contentDescription = "Dislikes Icon",
                    tint = LocalAppTheme.current.text
                )
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = "Likes Icon",
                    tint = LocalAppTheme.current.text
                )
                Text(
                    text = uiState.likesAmount.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = uiState.preparationsAmount.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Outlined.SoupKitchen, //TODO maybe change
                    contentDescription = "Preparations Icon",
                    tint = LocalAppTheme.current.text
                )
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.AutoMirrored.Outlined.Comment,
                    contentDescription = "Reviews Icon",
                    tint = LocalAppTheme.current.text
                )
                Text(
                    text = uiState.reviewsAmount.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            MultiLineTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = uiState.description,
                labelText = "Description"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.files.forEach { file ->
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onUiAction(EditedRecipeUiAction.ViewMediaFile(file))
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
                // TODO make delete files
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            mediaFilePickerManager.showDialog(FileType.ANY, recipeFileName + uiState.files.size) {
                                onUiAction(EditedRecipeUiAction.AddFile(it.file))
                            }
                        }
                        .padding(4.dp),
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = "Attach File Icon",
                    tint = LocalAppTheme.current.text
                )
            }
            HorizontalDivider(color = LocalAppTheme.current.text)
            Column {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Cooking time: " + uiState.cookingTime.toShortTimeString(),
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                EditedLabelContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    name = "Categories",
                    labels = uiState.categories.map { it.name }, // TODO optimize!!!
                    searchTitle = "Categories",
                    searchText = uiState.categoriesSearchText,
                    searchedLabels = uiState.searchedCategories.map { it.name },
                    maxRows = 2,
                    onContainerClick = {},
                    onLabelClick = {}
                )
                WeightedProductsLabelContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    name = "Products",
                    productDataList = uiState.products,
                    searchTitle = "Products",
                    searchText = uiState.productsSearchText,
                    searchedLabels = uiState.searchedProducts,
                    maxRows = 2,
                    onLabelCreate = { data ->
                        onUiAction(EditedRecipeUiAction.AddWeightedProduct(data))
                    },
                    onContainerClick = {},
                    onLabelClick = {}
                )
                // Provide db of categories and products from server and change text in zvit
            }
            if (uiState.isPublished) {
                HorizontalDivider(color = LocalAppTheme.current.text)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reviews",
                        style = LocalAppTheme.current.typography.bodyMedium
                    )
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                isReviewTextFieldDialogShown = true
                            }
                            .padding(4.dp),
                        imageVector = Icons.Outlined.AddAPhoto,
                        contentDescription = "Camera Button",
                        tint = LocalAppTheme.current.text
                    )
                }
                // TODO add nested scroll
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(reviewsUiState.reviews, key = { it.id }) { reviewData ->
                        ReviewItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            reviewItemData = reviewData,
                            onProfileClick = {
                                onUiAction(EditedRecipeUiAction.NavigateToOtherProfile(reviewData.authorId))
                            },
                            onLikeClick = {
                                if (reviewData.isLiked) {
                                    onUiAction(EditedRecipeUiAction.DislikeReview(reviewData.id))
                                } else {
                                    onUiAction(EditedRecipeUiAction.LikeReview(reviewData.id))
                                }
                            }
                        )
                    }
                }
            }
        }
        NavBar(
            modifier = Modifier.fillMaxWidth(),
            onHomeClick = {
                onUiAction(EditedRecipeUiAction.NavigateToHome)
            },
            onAddClick = {},
            onProfileClick = {
                onUiAction(EditedRecipeUiAction.NavigateToProfile)
            }
        )
        BottomInsetSpacer()
    }

    MediaPickMethodDialog(mediaFilePickerManager)

    ReviewTextFieldDialog(
        isShown = isReviewTextFieldDialogShown,
        reviewState = uiState.reviewText,
        onSendReview = {
            onUiAction(EditedRecipeUiAction.SendReview)
            isReviewTextFieldDialogShown = false
        },
        onDismiss = {
            isReviewTextFieldDialogShown = false
        }
    )
}