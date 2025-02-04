package com.letthemcook.profile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.items.RecipeItemData

@Composable
fun RecipesGrid(
    modifier: Modifier = Modifier,
    recipes: List<RecipeItemData>,
    onRecipeClick: (RecipeItemData) -> Unit
) {
    LazyHorizontalGrid(
        modifier = modifier,
        rows = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            val recipeImage = recipe.bitmap
            if (recipeImage == null) {
                Image(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable {
                            onRecipeClick(recipe)
                        },
                    painter = painterResource(id = com.letthemcook.theme.R.drawable.image_placeholder),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.FillBounds
                )
            } else {
                Image(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable {
                            onRecipeClick(recipe)
                        },
                    bitmap = recipeImage.asImageBitmap(),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}