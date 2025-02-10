package com.letthemcook.profile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letthemcook.profile.R
import com.letthemcook.profile.domain.viewModels.settings.SettingsUiAction
import com.letthemcook.profile.domain.viewModels.settings.SettingsUiState
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.base.Theme
import com.letthemcook.theme.components.buttons.SwitchButton
import com.letthemcook.theme.language.Language
import com.letthemcook.theme.screensContainer.LocalScreenContainer

// TODO check dark theme for feed and editor and cooking!

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onUiAction: (SettingsUiAction) -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    LaunchedEffect(Unit) {
        screenContainer.apply {
            clearToDefaults()

            setShowToolBar(true)
            setOnToolBarBackClick { onUiAction(SettingsUiAction.NavigateBack) }

            setShowNavigationBar(true)
            setOnNavigateToHome { onUiAction(SettingsUiAction.NavigateToHome) }
            setOnNavigateToNewRecipe { onUiAction(SettingsUiAction.NavigateToNewRecipe) }
            setOnNavigateToProfile { onUiAction(SettingsUiAction.NavigateToProfile) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = LocalAppTheme.current.typography.titleMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_theme),
                    style = LocalAppTheme.current.typography.titleSmall
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = "Palette Icon",
                    tint = LocalAppTheme.current.text
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .run {
                            if (uiState.currentTheme == Theme.LIGHT) {
                                border(1.dp, LocalAppTheme.current.text, RoundedCornerShape(16.dp))
                            } else this
                        }
                        .padding(8.dp),
                    imageVector = Icons.Outlined.LightMode,
                    contentDescription = "Light Mode Icon",
                    tint = LocalAppTheme.current.text
                )
                SwitchButton(
                    modifier = Modifier.size(64.dp, 40.dp),
                    isLookingRight = uiState.currentTheme == Theme.DARK,
                    onClick = {
                        onUiAction(SettingsUiAction.ToggleTheme)
                    }
                )
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .run {
                            if (uiState.currentTheme == Theme.DARK) {
                                border(1.dp, LocalAppTheme.current.text, RoundedCornerShape(16.dp))
                            } else this
                        }
                        .padding(8.dp),
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = "Dark Mode Icon",
                    tint = LocalAppTheme.current.text
                )
            }
            HorizontalDivider(color = LocalAppTheme.current.text)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.language),
                    style = LocalAppTheme.current.typography.titleSmall
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Language,
                    contentDescription = "Language Icon",
                    tint = LocalAppTheme.current.text
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onUiAction(SettingsUiAction.SetLanguage(Language.ENGLISH))
                    }
                    .run {
                        if (uiState.currentLanguage == Language.ENGLISH) {
                            border(1.dp, LocalAppTheme.current.text, RoundedCornerShape(16.dp))
                        } else this
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.english),
                    style = LocalAppTheme.current.typography.titleSmall
                )
                Text(
                    text = "\uD83C\uDDEC\uD83C\uDDE7",
                    style = LocalAppTheme.current.typography.titleSmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onUiAction(SettingsUiAction.SetLanguage(Language.UKRAINIAN))
                    }
                    .run {
                        if (uiState.currentLanguage == Language.UKRAINIAN) {
                            border(1.dp, LocalAppTheme.current.text, RoundedCornerShape(16.dp))
                        } else this
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.ukrainian),
                    style = LocalAppTheme.current.typography.titleSmall
                )
                Text(
                    text = "\uD83C\uDDFA\uD83C\uDDE6",
                    style = LocalAppTheme.current.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .clickable {
                        onUiAction(SettingsUiAction.LogOut)
                    }
                    .clip(RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    modifier = Modifier.padding(6.dp),
                    text = stringResource(R.string.log_out),
                    style = LocalAppTheme.current.typography.bodyLarge,
                    textDecoration = TextDecoration.Underline
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Log Out Icon",
                    tint = LocalAppTheme.current.text
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}