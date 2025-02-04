package com.letthemcook.feed.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.items.UserItemData
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.images.ProfileImage

@Composable
fun UserItem(
    modifier: Modifier = Modifier,
    userItemData: UserItemData,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        ProfileImage(
            modifier = Modifier.width(64.dp),
            bitmap = userItemData.bitmap
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "@${userItemData.login}",
                style = LocalAppTheme.current.typography.bodyMedium
            )
            Text(
                modifier = Modifier.alpha(0.66f),
                text = "${userItemData.name} ${userItemData.surname}",
                style = LocalAppTheme.current.typography.bodySmall
            )
            Text(
                modifier = Modifier.alpha(0.66f),
                text = "${userItemData.totalFollowers} followers",
                style = LocalAppTheme.current.typography.bodySmall
            )
        }
    }
}