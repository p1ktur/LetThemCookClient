package com.cooking.media.ui.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerForLocalUiAction
import com.cooking.media.domain.viewModels.mediaViewer.MediaViewerForLocalViewModel
import com.cooking.media.domain.viewModels.mediaViewerImage.MediaViewerImageUiAction
import com.cooking.media.domain.viewModels.mediaViewerImage.MediaViewerImageViewModel
import com.cooking.media.ui.screens.MediaViewerImageScreen
import com.cooking.media.ui.screens.MediaViewerScreen
import com.letthemcook.core.domain.model.file.File
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

sealed interface MediaNavRoutes {
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

fun NavGraphBuilder.addMediaRoutes(
    navController: NavController
) {
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
                    MediaViewerForLocalUiAction.NavigateBack -> navController.navigateUp()
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
                    MediaViewerImageUiAction.NavigateBack -> navController.navigateUp()
                }
                viewModel.onUiAction(action)
            }
        )
    }
}