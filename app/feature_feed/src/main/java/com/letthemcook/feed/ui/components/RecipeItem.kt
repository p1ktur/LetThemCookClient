package com.letthemcook.feed.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.core.domain.format.prettyString
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.core.domain.model.status.LikeStatus
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun RecipeItem(
    modifier: Modifier = Modifier,
    recipeItemData: RecipeItemData,
    index: Int,
    lastIndex: Int,
    onClick: () -> Unit,
    onUserLoginClick: () -> Unit
) {
    val recipeBitmap = remember(recipeItemData) { recipeItemData.bitmap }

    Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (recipeBitmap != null) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                bitmap = recipeBitmap.asImageBitmap(),
                contentDescription = "Recipe Image",
                contentScale = ContentScale.FillWidth
            )
        } else {
            Spacer(modifier = Modifier.height(0.dp))
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = recipeItemData.dislikesAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = if (recipeItemData.likeStatus == LikeStatus.DISLIKED) {
                    Icons.Filled.ThumbDown
                } else {
                    Icons.Outlined.ThumbDown
                },
                contentDescription = "Dislikes Icon",
                tint = LocalAppTheme.current.text
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = if (recipeItemData.likeStatus == LikeStatus.LIKED) {
                    Icons.Filled.ThumbUp
                } else {
                    Icons.Outlined.ThumbUp
                },
                contentDescription = "Likes Icon",
                tint = LocalAppTheme.current.text
            )
            Text(
                text = recipeItemData.likesAmount.cute(),
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
                text = recipeItemData.viewsAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Outlined.SoupKitchen,
                contentDescription = "Preparations Icon",
                tint = LocalAppTheme.current.text
            )
            Text(
                text = recipeItemData.preparationsAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = "Reviews Icon",
                tint = LocalAppTheme.current.text
            )
            Text(
                text = recipeItemData.reviewsAmount.cute(),
                style = LocalAppTheme.current.typography.bodyMedium
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onUserLoginClick)
                    .padding(4.dp),
                text = "@${recipeItemData.authorLogin}",
                style = LocalAppTheme.current.typography.bodySmall
            )
            recipeItemData.publicationDate?.let { date ->
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = date.prettyString(),
                    style = LocalAppTheme.current.typography.bodySmall
                )
            }
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            text = recipeItemData.name,
            style = LocalAppTheme.current.typography.bodyLarge,
        )
        if (recipeItemData.description.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                text = recipeItemData.description,
                style = LocalAppTheme.current.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (index != lastIndex) {
            HorizontalDivider(color = LocalAppTheme.current.text)
        } else {
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}