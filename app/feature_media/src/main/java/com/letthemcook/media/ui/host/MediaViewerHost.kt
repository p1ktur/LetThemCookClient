package com.letthemcook.media.ui.host

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.letthemcook.media.domain.viewModels.mediaViewer.MediaViewerUiAction
import com.letthemcook.media.domain.viewModels.mediaViewer.MediaViewerViewModel
import com.letthemcook.media.domain.viewModels.mediaViewerImage.MediaViewerImageUiAction
import com.letthemcook.media.domain.viewModels.mediaViewerImage.MediaViewerImageViewModel
import com.letthemcook.media.ui.screens.MediaViewerImageScreen
import com.letthemcook.media.ui.screens.MediaViewerScreen
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.domain.model.file.extensions.toBytes
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.theme.screensContainer.LocalScreenContainer
import com.letthemcook.theme.ui.navigation.MediaViewerAccess
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

sealed interface MediaNavRoutes {
    @Serializable data object None : MediaNavRoutes
    @Serializable data class MediaViewerForLocal(val file: File, val params: RemoteFileManager.RequestParams?) :
        MediaNavRoutes
    @Serializable data class MediaViewerImage(val bitmapBytes: ByteArray) : MediaNavRoutes {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MediaViewerImage

            return bitmapBytes.contentEquals(other.bitmapBytes)
        }

        override fun hashCode(): Int {
            return bitmapBytes.contentHashCode()
        }
    }
}

@Composable
fun MediaViewerHost(
    mediaViewerAccessState: MutableState<MediaViewerAccess>
) {
    val navController = rememberNavController()
    val screenContainer = LocalScreenContainer.current

    LaunchedEffect(Unit) {
        mediaViewerAccessState.value = MediaViewerAccess(
            stopViewing = {
                navController.popBackStack(MediaNavRoutes.None, false)
                screenContainer.setViewingMedia(false)
            },
            viewFile = { file, params ->
                navController.navigate(MediaNavRoutes.MediaViewerForLocal(file, params))
                screenContainer.setViewingMedia(true)
            },
            viewBitmap = {
                navController.navigate(MediaNavRoutes.MediaViewerImage(it.toBytes()))
                screenContainer.setViewingMedia(true)
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = MediaNavRoutes.None,
        enterTransition = { EnterTransition.None },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<MediaNavRoutes.None> {}
        composable<MediaNavRoutes.MediaViewerForLocal>(
            typeMap = mapOf(
                typeOf<File>() to FileNavType,
                typeOf<RemoteFileManager.RequestParams?>() to RemoteFileManagerRequestParamsNavType
            )
        ) { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<MediaNavRoutes.MediaViewerForLocal>()
            val file = route.file
            val params = route.params

            val viewModel = koinViewModel<MediaViewerViewModel>(parameters = { parametersOf(file, params) })
            val uiState by viewModel.uiState.collectAsState()

            MediaViewerScreen(
                uiState = uiState,
                onUiAction = { action ->
                    when (action) {
                        MediaViewerUiAction.Close -> {
                            navController.popBackStack(MediaNavRoutes.None, false)
                            screenContainer.setViewingMedia(false)
                        }
                    }
                    viewModel.onUiAction(action)
                }
            )
        }
        composable<MediaNavRoutes.MediaViewerImage>(
            typeMap = mapOf(
                typeOf<ByteArray>() to ByteArrayNavType
            )
        ) { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<MediaNavRoutes.MediaViewerImage>()
            val bitmapBytes = route.bitmapBytes

            val viewModel = koinViewModel<MediaViewerImageViewModel>(parameters = { parametersOf(bitmapBytes) })
            val uiState by viewModel.uiState.collectAsState()

            MediaViewerImageScreen(
                uiState = uiState,
                onUiAction = { action ->
                    when (action) {
                        MediaViewerImageUiAction.Close -> {
                            navController.popBackStack(MediaNavRoutes.None, false)
                            screenContainer.setViewingMedia(false)
                        }
                    }
                    viewModel.onUiAction(action)
                }
            )
        }
    }
}