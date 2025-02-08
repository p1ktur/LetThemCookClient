package com.letthemcook.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.rememberTextMeasurer
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiAction
import com.letthemcook.editor.domain.viewModels.cooking.CookingUiState
import com.letthemcook.editor.ui.components.canvas.CookingCanvas
import com.letthemcook.editor.ui.components.cooking.CookingControlPanel
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.screensContainer.LocalScreenContainer

@Composable
fun CookingScreen(
    uiState: CookingUiState,
    onUiAction: (CookingUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setToolBarStatusText(uiState.recipeName)
            setOnToolBarBackClick { onUiAction(CookingUiAction.NavigateBack) }

            setShowNavigationBar(false)
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val blockComponentTitleTextStyle = MaterialTheme.typography.titleLarge
    val blockComponentNameTextStyle = MaterialTheme.typography.bodyLarge
    val blockComponentContentTextStyle = MaterialTheme.typography.bodyMedium

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        CookingControlPanel(
            uiState = uiState,
            onUiAction = onUiAction
        )
        CookingCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RectangleShape),
            uiState = uiState,
            textMeasurer = textMeasurer,
            blockComponentTitleTextStyle = blockComponentTitleTextStyle,
            blockComponentNameTextStyle = blockComponentNameTextStyle,
            blockComponentContentTextStyle = blockComponentContentTextStyle,
            onUiAction = onUiAction
        )
    }
}