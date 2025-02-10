package com.letthemcook.profile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.model.items.RecipeItemData
import com.letthemcook.profile.R
import com.letthemcook.theme.base.LocalAppTheme

enum class PageOption {
    PUBLISHED,
    ARCHIVED
}

fun LazyListScope.recipesPager(
    publishedRecipes: List<RecipeItemData>,
    archivedRecipes: List<RecipeItemData>,
    recipesByThree: List<Array<RecipeItemData?>>,
    selectedOption: PageOption,
    onSetSelectedOption: (PageOption) -> Unit,
    onPublishedRecipeClick: (RecipeItemData) -> Unit,
    onArchivedRecipeClick: (RecipeItemData) -> Unit
) {
    if (publishedRecipes.isEmpty() && archivedRecipes.isEmpty()) return

    item {
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = LocalAppTheme.current.text)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.your_recipes),
            style = LocalAppTheme.current.typography.bodyLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (publishedRecipes.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSetSelectedOption(PageOption.PUBLISHED)
                        }
                        .padding(vertical = 8.dp),
                    text = stringResource(R.string.published),
                    style = LocalAppTheme.current.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    textDecoration = if (selectedOption == PageOption.PUBLISHED) TextDecoration.Underline else null
                )
            }
            if (archivedRecipes.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSetSelectedOption(PageOption.ARCHIVED)
                        }
                        .padding(vertical = 8.dp),
                    text = stringResource(R.string.archived),
                    style = LocalAppTheme.current.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    textDecoration = if (selectedOption == PageOption.ARCHIVED) TextDecoration.Underline else null
                )
            }
        }
    }

    when (selectedOption) {
        PageOption.PUBLISHED -> {
            recipesGrid(
                recipesByThree = recipesByThree,
                onRecipeClick = onPublishedRecipeClick
            )
        }
        PageOption.ARCHIVED -> {
            recipesGrid(
                recipesByThree = recipesByThree,
                onRecipeClick = onArchivedRecipeClick
            )
        }
    }
}