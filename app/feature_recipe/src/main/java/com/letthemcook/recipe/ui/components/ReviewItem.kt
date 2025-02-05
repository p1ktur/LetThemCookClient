package com.letthemcook.recipe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.items.ReviewItemData
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.images.ProfileImage

@Composable
fun ReviewItem(
    modifier: Modifier = Modifier,
    reviewItemData: ReviewItemData,
    onProfileClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileImage(
            modifier = Modifier.width(64.dp),
            bitmap = reviewItemData.authorBitmap,
            onClick = { onProfileClick() }
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = reviewItemData.authorLogin,
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = reviewItemData.text,
                style = LocalAppTheme.current.typography.bodySmall
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reviewItemData.likesAmount.toString(),
                style = LocalAppTheme.current.typography.bodySmall
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLikeClick),
                imageVector = if (reviewItemData.isLiked) {
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