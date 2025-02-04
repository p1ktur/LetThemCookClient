package com.letthemcook.theme.components.images

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    bitmap: Bitmap?,
    onClick: ((Bitmap) -> Unit)? = null
) {
    if (bitmap == null) {
        Image(
            modifier = modifier
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(LocalAppTheme.current.screenThree),
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Image",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(LocalAppTheme.current.text, BlendMode.SrcAtop)
        )
    } else {
        Image(
            modifier = modifier
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(LocalAppTheme.current.screenThree)
                .run {
                    if (onClick != null) {
                        clickable { onClick(bitmap) }
                    } else {
                        this
                    }
                },
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop
        )
    }
}