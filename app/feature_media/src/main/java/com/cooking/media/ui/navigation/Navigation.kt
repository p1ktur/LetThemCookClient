package com.cooking.media.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerUiAction
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerViewModel
import com.cooking.media.ui.screens.MediaViewerScreen
import com.letthemcook.core.domain.model.data.file.File
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

sealed interface MediaNavRoutes {
    @Serializable data class MediaViewer(val file: File) : MediaNavRoutes
}

fun NavGraphBuilder.addMediaRoutes(
    navController: NavController
) {
    composable<MediaNavRoutes.MediaViewer>(
        typeMap = mapOf(
            typeOf<File>() to FileNavType()
        )
    ) { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<MediaNavRoutes.MediaViewer>()
        val file = route.file

        val viewModel = koinViewModel<MediaViewerViewModel>(parameters = { parametersOf(file) })
        val uiState by viewModel.uiState.collectAsState()

        MediaViewerScreen(
            uiState = uiState,
            onUiAction = { action ->
                when (action) {
                    MediaViewerUiAction.NavigateBack -> navController.navigateUp()
                }
                viewModel.onUiAction(action)
            }
        )
    }
}