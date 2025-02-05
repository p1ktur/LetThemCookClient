package com.cooking.media.ui.host

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerForLocalUiAction
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerForLocalViewModel
import com.cooking.media.domain.viewModels.mediaViewerImage.MediaViewerImageUiAction
import com.cooking.media.domain.viewModels.mediaViewerImage.MediaViewerImageViewModel
import com.cooking.media.ui.screens.MediaViewerImageScreen
import com.cooking.media.ui.screens.MediaViewerScreen
import com.letthemcook.core.domain.media.toBytes
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.theme.ui.navigation.MediaViewerAccess
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

sealed interface MediaNavRoutes {
    @Serializable data object None : MediaNavRoutes
    @Serializable data class MediaViewerForLocal(val file: File) : MediaNavRoutes
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

    LaunchedEffect(Unit) {
        mediaViewerAccessState.value = MediaViewerAccess(
            stopViewing = { navController.popBackStack(MediaNavRoutes.None, false) },
            viewFile = { navController.navigate(MediaNavRoutes.MediaViewerForLocal(it)) },
            viewBitmap = { navController.navigate(MediaNavRoutes.MediaViewerImage(it.toBytes())) }
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
                typeOf<File>() to FileNavType()
            )
        ) { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<MediaNavRoutes.MediaViewerForLocal>()
            val file = route.file

            val viewModel = koinViewModel<MediaViewerForLocalViewModel>(parameters = { parametersOf(file) })
            val uiState by viewModel.uiState.collectAsState()

            MediaViewerScreen(
                uiState = uiState,
                onUiAction = { action ->
                    when (action) {
                        MediaViewerForLocalUiAction.Close -> navController.popBackStack(MediaNavRoutes.None, false)
                    }
                    viewModel.onUiAction(action)
                }
            )
        }
        composable<MediaNavRoutes.MediaViewerImage>(
            typeMap = mapOf(
                typeOf<ByteArray>() to ByteArrayNavType()
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
                        MediaViewerImageUiAction.Close -> navController.popBackStack(MediaNavRoutes.None, false)
                    }
                    viewModel.onUiAction(action)
                }
            )
        }
    }
}