package com.letthemcook.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.letthemcook.editor.R
import com.letthemcook.editor.domain.viewModels.tutorial.TutorialUiAction
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@Composable
fun TutorialScreen(
    onUiAction: (TutorialUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    val cookingTutorial = stringResource(R.string.cooking_tutorial)

    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setToolBarStatusText(cookingTutorial)
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