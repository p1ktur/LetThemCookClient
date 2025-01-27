package com.letthemcook.editor.ui.components.cooking

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.toShortTimeString
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiAction
import com.letthemcook.editor.ui.drawing.getBlockBodyBrush
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun BlockInfo(
    modifier: Modifier = Modifier,
    canvasCounter: Int,
    selectedBlock: BlockComponent,
    onUiAction: (CookingUiAction) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
            }
            .animateContentSize()
    ) {
        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(getBlockBodyBrush(LocalAppTheme.current.background, selectedBlock.colorOption.color))
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = selectedBlock.name,
                        style = LocalAppTheme.current.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .padding(4.dp),
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = "Expand Button",
                        tint = LocalAppTheme.current.text
                    )
                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable {
                                onUiAction(CookingUiAction.DeselectBlock)
                            }
                            .padding(4.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Button",
                        tint = LocalAppTheme.current.text
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    key(canvasCounter) {
                        Text(
                            text = selectedBlock.cookingState.toString(),
                            style = LocalAppTheme.current.typography.bodyLarge
                        )
                        Text(
                            text = selectedBlock.time.toShortTimeString(),
                            style = LocalAppTheme.current.typography.bodyLarge
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    text = selectedBlock.description,
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(getBlockBodyBrush(LocalAppTheme.current.background, selectedBlock.colorOption.color)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Block info",
                    style = LocalAppTheme.current.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .padding(4.dp),
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand Button",
                    tint = LocalAppTheme.current.text
                )
                Icon(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable {
                            onUiAction(CookingUiAction.DeselectBlock)
                        }
                        .padding(4.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Button",
                    tint = LocalAppTheme.current.text
                )
            }
        }
    }
}