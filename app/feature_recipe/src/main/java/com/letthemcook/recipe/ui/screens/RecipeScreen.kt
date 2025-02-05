package com.letthemcook.recipe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeUiAction
import com.letthemcook.recipe.domain.viewModels.recipe.RecipeUiState
import com.letthemcook.recipe.domain.viewModels.reviews.ReviewsUiState
import com.letthemcook.recipe.ui.components.ReviewItem
import com.letthemcook.recipe.ui.components.dialogs.ReviewTextFieldDialog
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.buttons.TextButton
import com.letthemcook.theme.components.labels.LabelContainer
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@Composable
fun RecipeScreen(
    uiState: RecipeUiState,
    reviewsUiState: ReviewsUiState,
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

    var isReviewTextFieldDialogShown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f / 4f)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = uiState.image
                        ?: ImageBitmap.imageResource(id = com.letthemcook.theme.R.drawable.image_placeholder),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.FillBounds
                )
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (uiState.isSaved) {
                                onUiAction(RecipeUiAction.RemoveFromSaved)
                            } else {
                                onUiAction(RecipeUiAction.Save)
                            }
                        }
                        .padding(4.dp)
                        .align(Alignment.TopEnd),
                    imageVector = if (uiState.isSaved) {
                        Icons.Default.Bookmark
                    } else {
                        Icons.Default.BookmarkBorder
                    },
                    contentDescription = "Save   Button",
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
                    Text(
                        text = uiState.name,
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                }
                TextButton(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopCenter),
                    text = "Start cooking",
                    onClick = {
                        onUiAction(RecipeUiAction.StartCooking)
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
                    style = LocalAppTheme.current.typography.bodySmall
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.ThumbDown,
                    contentDescription = "Dislikes Icon",
                    tint = LocalAppTheme.current.text
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = "Likes Icon",
                    tint = LocalAppTheme.current.text
                )
                Text(
                    text = uiState.likesAmount.toString(),
                    style = LocalAppTheme.current.typography.bodySmall
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = uiState.preparationsAmount.toString(),
                    style = LocalAppTheme.current.typography.bodySmall
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.SoupKitchen, //TODO maybe change
                    contentDescription = "Preparations Icon",
                    tint = LocalAppTheme.current.text
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.AutoMirrored.Outlined.Comment,
                    contentDescription = "Reviews Icon",
                    tint = LocalAppTheme.current.text
                )
                Text(
                    text = uiState.reviewsAmount.toString(),
                    style = LocalAppTheme.current.typography.bodySmall
                )
            }
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = uiState.description,
                style = LocalAppTheme.current.typography.bodyMedium
            )
            HorizontalDivider(color = LocalAppTheme.current.text)
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Cooking time: " + uiState.cookingTime.toShortTimeString(),
                style = LocalAppTheme.current.typography.bodyLarge
            )
            LabelContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                name = "Categories",
                labels = uiState.categories,
                maxRows = 2,
                onLabelClick = {}
            )
            LabelContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                name = "Products",
                labels = uiState.products,
                maxRows = 2,
                onLabelClick = {}
            )
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
                            onUiAction(RecipeUiAction.NavigateToOtherProfile(reviewData.authorId))
                        },
                        onLikeClick = {
                            if (reviewData.isLiked) {
                                onUiAction(RecipeUiAction.DislikeReview(reviewData.id))
                            } else {
                                onUiAction(RecipeUiAction.LikeReview(reviewData.id))
                            }
                        }
                    )
                }
            }
        }
    }

    ReviewTextFieldDialog(
        isShown = isReviewTextFieldDialogShown,
        reviewState = uiState.reviewText,
        onSendReview = {
            onUiAction(RecipeUiAction.SendReview)
            isReviewTextFieldDialogShown = false
        },
        onDismiss = {
            isReviewTextFieldDialogShown = false
        }
    )
}