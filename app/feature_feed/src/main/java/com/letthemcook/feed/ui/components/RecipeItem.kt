package com.letthemcook.feed.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun RecipeItem(
    modifier: Modifier = Modifier,
    recipeItemData: RecipeItemData,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(5f / 4f),
            bitmap = recipeItemData.bitmap?.asImageBitmap()
                ?: ImageBitmap.imageResource(id = com.letthemcook.theme.R.drawable.image_placeholder),
            contentDescription = "Recipe Image",
            contentScale = ContentScale.FillBounds
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart),
            text = recipeItemData.name,
            style = LocalAppTheme.current.typography.bodyLarge
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd),
            text = recipeItemData.authorLogin,
            style = LocalAppTheme.current.typography.bodyMedium
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = recipeItemData.dislikesAmount.toString(),
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
                        text = recipeItemData.likesAmount.toString(),
                        style = LocalAppTheme.current.typography.bodySmall
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = recipeItemData.preparationsAmount.toString(),
                            style = LocalAppTheme.current.typography.bodySmall
                        )
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Outlined.SoupKitchen, //TODO maybe change
                            contentDescription = "Dislikes Icon",
                            tint = LocalAppTheme.current.text
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = recipeItemData.reviewsAmount.toString(),
                            style = LocalAppTheme.current.typography.bodySmall
                        )
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.AutoMirrored.Outlined.Comment,
                            contentDescription = "Dislikes Icon",
                            tint = LocalAppTheme.current.text
                        )
                    }
                }
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                text = recipeItemData.description,
                style = LocalAppTheme.current.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}