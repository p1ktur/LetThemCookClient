package com.letthemcook.theme.components.spacers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.letthemcook.theme.base.LocalAppTheme

@Composable
fun TopInsetSpacer(color: Color = LocalAppTheme.current.screenThree) {
    Box(
        modifier = Modifier
            .windowInsetsTopHeight(WindowInsets.systemBars)
            .fillMaxWidth()
            .background(color)
    )
}

@Composable
fun BottomInsetSpacer(color: Color = LocalAppTheme.current.background) {
    Box(
        modifier = Modifier
            .windowInsetsBottomHeight(WindowInsets.navigationBars)
            .fillMaxWidth()
            .background(color)
    )
}