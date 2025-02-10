package com.letthemcook.feed.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.cute
import com.letthemcook.core.domain.model.items.UserItemData
import com.letthemcook.feed.R
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
                text = userItemData.totalFollowers.cute() + stringResource(R.string.followers),
                style = LocalAppTheme.current.typography.bodySmall
            )
        }
    }
}