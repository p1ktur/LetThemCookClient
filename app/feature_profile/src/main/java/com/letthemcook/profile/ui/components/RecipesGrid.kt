package com.letthemcook.profile.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.items.RecipeItemData

fun LazyListScope.recipesGrid(
    recipesByThree: List<Array<RecipeItemData?>>,
    onRecipeClick: (RecipeItemData) -> Unit
) {
    items(
        recipesByThree,
        key = {
            "${it.getOrNull(0)?.id}_${it.getOrNull(1)?.id}_${it.getOrNull(2)?.id}"
        }
    ) { threeRecipes ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            threeRecipes.forEach { recipe ->
                if (recipe != null) {
                    RecipeItemImage(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        bitmap = recipe.bitmap,
                        onClick = {
                            onRecipeClick(recipe)
                        }
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun LazyItemScope.RecipeItemImage(
    modifier: Modifier,
    bitmap: Bitmap?,
    onClick: () -> Unit
) {
    if (bitmap == null) {
        Image(
            modifier = modifier
                .animateItem()
                .clickable(onClick = onClick),
            painter = painterResource(id = com.letthemcook.theme.R.drawable.image_placeholder),
            contentDescription = "Recipe Image",
            contentScale = ContentScale.FillHeight
        )
    } else {
        Image(
            modifier = modifier
                .animateItem()
                .clickable(onClick = onClick),
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Recipe Image",
            contentScale = ContentScale.Crop
        )
    }
}