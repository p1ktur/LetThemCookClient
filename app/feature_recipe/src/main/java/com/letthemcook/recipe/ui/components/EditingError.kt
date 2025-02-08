package com.letthemcook.recipe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letthemcook.recipe.domain.model.EditingError
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun EditingError(error: EditingError?) {
    if (error == null) {
        Spacer(modifier = Modifier.height(0.dp))
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Icons.Default.Error,
                contentDescription = "Error Show Icon",
                tint = LocalAppTheme.current.errorText
            )
            Text(
                text = when (error) {
                    EditingError.EmptyName -> "Provide name for recipe, before publishing."
                    EditingError.TooManyCategories -> "Maximum amount of categories is 20."
                    EditingError.TooManyProducts -> "Maximum amount of products is 20."
                },
                style = LocalAppTheme.current.typography.bodySmall,
                color = LocalAppTheme.current.errorText
            )
        }
    }
}