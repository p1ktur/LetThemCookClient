package com.letthemcook.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.letthemcook.editor.domain.viewModels.tutorial.TutorialUiAction
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@Composable
fun TutorialScreen(
    onUiAction: (TutorialUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setToolBarStatusText("Cooking Tutorial")
            setOnToolBarBackClick { onUiAction(TutorialUiAction.NavigateBack) }

            setShowNavigationBar(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        // TODO content
        // Say that maximum amount of blocks is 30
    }
}