package com.letthemcook.theme.screensContainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.media.EmptyMediaFilePicker
import com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.media.MediaFilePicker
import com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.media.MediaFilePickerManager
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.bars.NavBar
import com.letthemcook.theme.components.bars.ToolBar
import com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.MediaPickMethodDialog
import com.letthemcook.theme.components.dialogs.bottom.writeReview.ReviewWriter
import com.letthemcook.theme.components.dialogs.bottom.writeReview.WriteReviewDialog
import com.letthemcook.theme.components.spacers.BottomInsetSpacer
import com.letthemcook.theme.components.spacers.TopInsetSpacer
import org.koin.compose.koinInject

typealias Function = (() -> Unit)?

class ScreenContainer {
    // Common
    fun clearToDefaults() {
        setViewingMedia(false)

        setToolBarStatusText(null)
        setOnToolBarBackClick(null)
        setOnToolBarSettingsClick(null)
        setOnToolBarSearchClick(null)

        setOnNavigateToHome(null)
        setOnNavigateToNewRecipe(null)
        setOnNavigateToProfile(null)
    }

    private val _mediaFilePicker: MutableState<MediaFilePicker> = mutableStateOf(
        EmptyMediaFilePicker
    )
    val mediaFilePicker by _mediaFilePicker

    internal fun setMediaFilePicker(value: MediaFilePicker) {
        _mediaFilePicker.value = value
    }

    private val _reviewWriter = mutableStateOf(ReviewWriter())
    val reviewWriter by _reviewWriter

    private val _viewingMedia = mutableStateOf(false)
    val viewingMedia by _viewingMedia

    fun setViewingMedia(value: Boolean) {
        _viewingMedia.value = value
    }

    // Tool Bar
    private val _showToolBar = mutableStateOf(false)
    val showToolBar by _showToolBar

    fun setShowToolBar(value: Boolean) {
        _showToolBar.value = value
    }

    private val _toolBarColor = mutableStateOf(Color.White)
    val toolBarColor by _toolBarColor

    fun setToolBarColor(value: Color) {
        _toolBarColor.value = value
    }

    private val _toolBarStatusText = mutableStateOf<String?>(null)
    val toolBarStatusText by _toolBarStatusText

    fun setToolBarStatusText(value: String?) {
        _toolBarStatusText.value = value
    }

    private val _onToolBarBackClick = mutableStateOf<Function>(null)
    val onToolBarBackClick by _onToolBarBackClick

    fun setOnToolBarBackClick(value: Function) {
        _onToolBarBackClick.value = value
    }

    private val _onToolBarSettingsClick = mutableStateOf<Function>(null)
    val onToolBarSettingsClick by _onToolBarSettingsClick

    fun setOnToolBarSettingsClick(value: Function) {
        _onToolBarSettingsClick.value = value
    }

    private val _onToolBarSearchClick = mutableStateOf<Function>(null)
    val onToolBarSearchClick by _onToolBarSearchClick

    fun setOnToolBarSearchClick(value: Function) {
        _onToolBarSearchClick.value = value
    }

    // Navigation Bar
    private val _showNavigationBar = mutableStateOf(false)
    val showNavigationBar by _showNavigationBar

    fun setShowNavigationBar(value: Boolean) {
        _showNavigationBar.value = value
    }

    private val _navigationBarColor = mutableStateOf(Color.White)
    val navigationBarColor by _navigationBarColor

    fun setNavigationBarColor(value: Color) {
        _navigationBarColor.value = value
    }

    private val _onNavigateToHome = mutableStateOf<Function>(null)
    val onNavigateToHome by _onNavigateToHome

    fun setOnNavigateToHome(value: Function) {
        _onNavigateToHome.value = value
    }

    private val _onNavigateToNewRecipe = mutableStateOf<Function>(null)
    val onNavigateToNewRecipe by _onNavigateToNewRecipe

    fun setOnNavigateToNewRecipe(value: Function) {
        _onNavigateToNewRecipe.value = value
    }

    private val _onNavigateToProfile = mutableStateOf<Function>(null)
    val onNavigateToProfile by _onNavigateToProfile

    fun setOnNavigateToProfile(value: Function) {
        _onNavigateToProfile.value = value
    }
}

val LocalScreenContainer = compositionLocalOf { ScreenContainer() }

@Composable
fun ScreensContainer(
    content: @Composable BoxScope.() -> Unit
) {
    val screenContainer = LocalScreenContainer.current
    val toolBarColor = LocalAppTheme.current.screenThree
    val navigationBarColor = LocalAppTheme.current.background

    val mediaFilePicker = koinInject<MediaFilePickerManager>()
    mediaFilePicker.RegisterLaunchers()

    LaunchedEffect(Unit) {
        screenContainer.apply {
            setToolBarColor(toolBarColor)
            setNavigationBarColor(navigationBarColor)

            setMediaFilePicker(mediaFilePicker)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppTheme.current.background)
    ) {
        TopInsetSpacer(LocalScreenContainer.current.toolBarColor)
        if (LocalScreenContainer.current.showToolBar) {
            ToolBar(
                modifier = Modifier.fillMaxWidth(),
                color = LocalScreenContainer.current.toolBarColor,
                statusText = LocalScreenContainer.current.toolBarStatusText,
                onBackClick = LocalScreenContainer.current.onToolBarBackClick,
                onSettingsClick = LocalScreenContainer.current.onToolBarSettingsClick,
                onSearchClick = LocalScreenContainer.current.onToolBarSearchClick,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(LocalAppTheme.current.background),
            contentAlignment = Alignment.Center,
            content = {
                content()
            }
        )
        if (LocalScreenContainer.current.showNavigationBar) {
            NavBar(
                modifier = Modifier.fillMaxWidth(),
                color = LocalScreenContainer.current.navigationBarColor,
                onHomeClick = LocalScreenContainer.current.onNavigateToHome,
                onNewRecipeClick = LocalScreenContainer.current.onNavigateToNewRecipe,
                onProfileClick = LocalScreenContainer.current.onNavigateToProfile
            )
        }
        BottomInsetSpacer(LocalScreenContainer.current.navigationBarColor)
    }

    MediaPickMethodDialog(mediaFilePicker)

    WriteReviewDialog(screenContainer.reviewWriter)
}