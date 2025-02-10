package com.letthemcook.recipe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.core.domain.model.items.ReviewItemData
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.images.ProfileImage

@Composable
fun ReviewItem(
    modifier: Modifier = Modifier,
    reviewItemData: ReviewItemData,
    onProfileClick: () -> Unit,
    onLike: () -> Unit,
    onDisLike: () -> Unit
) {
    var isLiked by remember { mutableStateOf(reviewItemData.isLiked) }
    var likesAmount by remember { mutableIntStateOf(reviewItemData.likesAmount) }

    Row(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileImage(
            modifier = Modifier.width(48.dp),
            bitmap = reviewItemData.authorBitmap,
            onClick = { onProfileClick() }
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "@${reviewItemData.authorLogin}",
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = reviewItemData.text,
                style = LocalAppTheme.current.typography.bodySmall
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = likesAmount.cute(),
                style = LocalAppTheme.current.typography.bodySmall
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable {
                        isLiked = !isLiked
                        if (isLiked) {
                            likesAmount++
                            onLike()
                        } else {
                            likesAmount--
                            onDisLike()
                        }
                    },
                imageVector = if (isLiked) {
                    Icons.Default.ThumbUp
                } else {
                    Icons.Outlined.ThumbUp
                },
                contentDescription = "Likes Icon",
                tint = LocalAppTheme.current.text
            )
        }
    }
}