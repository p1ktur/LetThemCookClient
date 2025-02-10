package com.letthemcook.recipe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.RemoveRedEye
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.core.domain.format.prettyString
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.core.domain.model.file.FileType
import com.letthemcook.core.domain.model.status.LikeStatus
import com.letthemcook.recipe.R
import com.letthemcook.recipe.domain.model.LoadingStatus
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeUiAction
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeUiState
import com.letthemcook.recipe.ui.components.ReviewItem
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.images.RecipeImage
import com.letthemcook.theme.components.labels.LabelContainer
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import com.letthemcook.theme.ui.screens.LoadingScreen

@Composable
fun RecipeScreen(
    uiState: RecipeUiState,
    onUiAction: (RecipeUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarBackClick { onUiAction(RecipeUiAction.NavigateBack) }

            setShowNavigationBar(true)
            setOnNavigateToHome { onUiAction(RecipeUiAction.NavigateToHome) }
            setOnNavigateToNewRecipe { onUiAction(RecipeUiAction.NavigateToNewRecipe) }
            setOnNavigateToProfile { onUiAction(RecipeUiAction.NavigateToProfile) }
        }
    }

    val reviewWriter = LocalScreenContainer.current.reviewWriter

    val categoryLabelNames = remember(uiState.categories) { uiState.categories.map { it.name } }
    val productLabelNames = remember(uiState.products) { uiState.products.map { it.toString() } }

    val lazyGridState = rememberLazyStaggeredGridState()

    val isScrolledToBottom by remember {
        derivedStateOf {
            !lazyGridState.canScrollForward && lazyGridState.canScrollBackward
        }
    }

    LaunchedEffect(isScrolledToBottom) {
        if (isScrolledToBottom && !uiState.loadingReviews && uiState.reviews.isNotEmpty()) {
            onUiAction(RecipeUiAction.LoadReviews)
        }
    }

    LaunchedEffect(Unit) {
        onUiAction(RecipeUiAction.LoadData)
    }

    when (uiState.loadingStatus) {
        LoadingStatus.LOADING -> {
            LoadingScreen()
            return
        }
        LoadingStatus.FAILED -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalAppTheme.current.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.failed_to_load_recipe),
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                if (uiState.isOwner) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .size(120.dp, 40.dp),
                        text = stringResource(R.string.edit),
                        onClick = {
                            onUiAction(RecipeUiAction.EditRecipe)
                        }
                    )
                }
            }
            return
        }
        LoadingStatus.SUCCESS -> Unit
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    if (uiState.recipeBitmap != null) {
                        RecipeImage(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .width(140.dp),
                            bitmap = uiState.recipeBitmap,
                            clipToRoundedRect = true,
                            onClick = { bitmap ->
                                onUiAction(RecipeUiAction.ViewRecipeBitmap(bitmap))
                            }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.publicationDate?.let { date ->
                            Text(
                                text = date.prettyString(),
                                style = LocalAppTheme.current.typography.bodySmall
                            )
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .basicMarquee(),
                            text = uiState.name,
                            style = LocalAppTheme.current.typography.bodyLarge,
                            maxLines = 1
                        )
                        Text(
                            text = if (uiState.cookingTime != null) {
                                stringResource(R.string.cooking_time) + uiState.cookingTime.toShortTimeString()
                            } else {
                                stringResource(R.string.no_cooking_yet)
                            },
                            style = LocalAppTheme.current.typography.bodyLarge
                        )
                        if (uiState.recipeJson != null && uiState.recipeJson != "null") {
                            TextButton(
                                modifier = Modifier.size(130.dp, 40.dp),
                                text = stringResource(R.string.cook),
                                onClick = {
                                    onUiAction(RecipeUiAction.Cook(uiState.recipeJson))
                                }
                            )
                        }
                        if (uiState.isOwner) {
                            TextButton(
                                modifier = Modifier.size(130.dp, 40.dp),
                                text = stringResource(R.string.edit),
                                onClick = {
                                    onUiAction(RecipeUiAction.EditRecipe)
                                }
                            )
                        }
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
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (uiState.likeStatus == LikeStatus.DISLIKED) {
                                    onUiAction(RecipeUiAction.UnDislikeRecipe)
                                } else {
                                    onUiAction(RecipeUiAction.DislikeRecipe)
                                }
                            },
                        imageVector = if (uiState.likeStatus == LikeStatus.DISLIKED) {
                            Icons.Filled.ThumbDown
                        } else {
                            Icons.Outlined.ThumbDown
                        },
                        contentDescription = "Dislikes Icon",
                        tint = LocalAppTheme.current.text
                    )
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (uiState.likeStatus == LikeStatus.LIKED) {
                                    onUiAction(RecipeUiAction.UnlikeRecipe)
                                } else {
                                    onUiAction(RecipeUiAction.LikeRecipe)
                                }
                            },
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
                    if (!uiState.isOwner) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (uiState.isFavored) {
                                        onUiAction(RecipeUiAction.RemoveFromFavored)
                                    } else {
                                        onUiAction(RecipeUiAction.Favor)
                                    }
                                },
                            imageVector = if (uiState.isFavored) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Outlined.Bookmark
                            },
                            contentDescription = "Favor Button",
                            tint = LocalAppTheme.current.text
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
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
                if (uiState.description.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        text = uiState.description,
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                }
                if (uiState.attachments.isNotEmpty()) {
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
                        uiState.attachments.forEach { file ->
                            Icon(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        onUiAction(RecipeUiAction.ViewMediaFile(file))
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
                }
                HorizontalDivider(color = LocalAppTheme.current.text)
                Column {
                    LabelContainer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        name = stringResource(R.string.categories),
                        labels = categoryLabelNames,
                        maxRows = 2,
                        onLabelClick = {}
                    )
                    LabelContainer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        name = stringResource(R.string.products),
                        labels = productLabelNames,
                        maxRows = 2,
                        onLabelClick = {}
                    )
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .animateItem(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(color = LocalAppTheme.current.text)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.reviews),
                        style = LocalAppTheme.current.typography.bodyMedium
                    )
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                reviewWriter.showDialog(
                                    onSendReviewCallback = { text ->
                                        onUiAction(RecipeUiAction.SendReview(text))
                                    }
                                )
                            }
                            .padding(4.dp),
                        imageVector = Icons.AutoMirrored.Outlined.Comment,
                        contentDescription = "Add Review Button",
                        tint = LocalAppTheme.current.text
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (uiState.reviews.isNotEmpty()) {
            itemsIndexed(uiState.reviews, key = { _, it -> it.id }) { index, reviewData ->
                ReviewItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    reviewItemData = reviewData,
                    onProfileClick = {
                        onUiAction(RecipeUiAction.NavigateToOtherProfile(reviewData.authorId))
                    },
                    onLike = {
                        onUiAction(RecipeUiAction.LikeReview(index))
                    },
                    onDisLike = {
                        onUiAction(RecipeUiAction.UnlikeReview(index))
                    }
                )
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.no_reviews_yet),
                    style = LocalAppTheme.current.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}