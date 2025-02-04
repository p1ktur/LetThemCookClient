package com.letthemcook.theme.components.labels

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun LabelContainer(
    modifier: Modifier = Modifier,
    name: String,
    labels: List<String>,
    maxRows: Int = 3,
    isLoading: Boolean = false,
    onScrolledToEnd: (() -> Unit)? = null,
    onLabelClick: (Int) -> Unit
) {
    val lazyGridState = rememberLazyStaggeredGridState()

    val isScrolledToBottom by remember {
        derivedStateOf {
            !lazyGridState.canScrollForward && lazyGridState.canScrollBackward
        }
    }

    LaunchedEffect(isScrolledToBottom) {
        if (isScrolledToBottom && !isLoading) onScrolledToEnd?.invoke()
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = name,
            style = LocalAppTheme.current.typography.bodyLarge
        )
        LazyHorizontalStaggeredGrid(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = (maxRows * 36).dp),
            state = lazyGridState,
            rows = StaggeredGridCells.FixedSize(28.dp),
            horizontalItemSpacing = 8.dp,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(labels) { index, label ->
                LabelItem(
                    modifier = Modifier.animateItem(),
                    text = label,
                    icon = LabelIcon.NONE,
                    onClick = {
                        onLabelClick(index)
                    }
                )
            }
        }
    }
}