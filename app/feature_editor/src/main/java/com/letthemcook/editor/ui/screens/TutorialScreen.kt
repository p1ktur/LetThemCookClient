package com.letthemcook.editor.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.tutorial_1),
            style = LocalAppTheme.current.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.tutorial_2),
                style = LocalAppTheme.current.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
            Image(
                modifier = Modifier.weight(1f),
                painter = painterResource(id = R.drawable.tutorial_2),
                contentDescription = "Tutorial Image",
                contentScale = ContentScale.FillWidth
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.weight(1f),
                painter = painterResource(id = R.drawable.tutorial_3),
                contentDescription = "Tutorial Image",
                contentScale = ContentScale.FillWidth
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.tutorial_3),
                style = LocalAppTheme.current.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.tutorial_4),
                style = LocalAppTheme.current.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
            Image(
                modifier = Modifier.weight(1f),
                painter = painterResource(id = R.drawable.tutorial_4),
                contentDescription = "Tutorial Image",
                contentScale = ContentScale.FillWidth
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.weight(1f),
                painter = painterResource(id = R.drawable.tutorial_5),
                contentDescription = "Tutorial Image",
                contentScale = ContentScale.FillWidth
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.tutorial_5),
                style = LocalAppTheme.current.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.tutorial_6),
                style = LocalAppTheme.current.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
            Image(
                modifier = Modifier.weight(1f),
                painter = painterResource(id = R.drawable.tutorial_6),
                contentDescription = "Tutorial Image",
                contentScale = ContentScale.FillWidth
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.weight(1f),
                painter = painterResource(id = R.drawable.tutorial_7),
                contentDescription = "Tutorial Image",
                contentScale = ContentScale.FillWidth
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.tutorial_7),
                style = LocalAppTheme.current.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}