package com.letthemcook.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.letthemcook.editor.domain.viewModels.tutorial.TutorialUiAction
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer

@Composable
fun TutorialScreen(
    onUiAction: (TutorialUiAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer()
        ToolBar(
            modifier = Modifier.fillMaxWidth(),
            onBackClick = {
                onUiAction(TutorialUiAction.NavigateBack)
            }
        )

        // TODO content

        BottomInsetSpacer()
    }
}