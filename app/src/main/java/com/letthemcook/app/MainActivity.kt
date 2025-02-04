package com.letthemcook.app

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cooking.media.ui.navigation.MediaNavRoutes
import com.cooking.media.ui.navigation.addMediaRoutes
import com.letthemcook.auth.ui.navigation.AuthNavRoutes
import com.letthemcook.auth.ui.navigation.addAuthRoutes
import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.domain.media.toBytes
import com.letthemcook.core.domain.model.auth.tokens.TokenCheckResult
import com.letthemcook.core.domain.model.file.File
import com.letthemcook.core.ui.navigation.NavBarRoutes
import com.letthemcook.editor.ui.navigation.EditorNavRoutes
import com.letthemcook.editor.ui.navigation.addEditorRoutes
import com.letthemcook.feed.ui.navigation.FeedNavRoutes
import com.letthemcook.feed.ui.navigation.addFeedRoutes
import com.letthemcook.profile.ui.navigation.ProfileNavRoutes
import com.letthemcook.profile.ui.navigation.addProfileRoutes
import com.letthemcook.recipe.ui.navigation.RecipeNavRoutes
import com.letthemcook.recipe.ui.navigation.addRecipeRoutes
import com.letthemcook.theme.base.LetThemCookTheme
import com.letthemcook.theme.base.Theme
import com.letthemcook.theme.language.Language
import com.letthemcook.theme.providers.LanguageStateProvider
import com.letthemcook.theme.providers.ThemeStateProvider
import com.letthemcook.theme.ui.screens.LoadingScreen
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val navBarRoutes = NavBarRoutes(
        homeRoute = FeedNavRoutes.Feed,
        addRoute = RecipeNavRoutes.EditedRecipe(null),
        profileRoute = ProfileNavRoutes.EditedProfile
//        profileRoute = ProfileNavRoutes.Profile("aa6cc1f7-a6d9-4de1-87dd-3fa62095dd3b")
    )

    private val authManager by inject<AuthManager>()

    //TODO limits on blocks and products and categories
    //TODO move toolbar and navbar to here
    //TODO check file sizes upon choosing them
    //TODO check internet connection on start and allow offline usage?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeStateProvider by inject<ThemeStateProvider>()
        val languageStateProvider by inject<LanguageStateProvider>()

        setContent {
            val isLoggedIn = remember { mutableStateOf<Boolean?>(null) }

            val navController = rememberNavController()
            val currentBackStackEntry by navController.currentBackStackEntryAsState()

            val theme by themeStateProvider.getTheme().collectAsState(Theme.LIGHT)
            val language by languageStateProvider.getLanguage().collectAsState(Language.ENGLISH)

            LaunchedEffect(language) {
                setLocale(
                    when (language) {
                        Language.ENGLISH -> "us"
                        Language.UKRAINIAN -> "ua"
                    }
                )
            }

            LaunchedEffect(currentBackStackEntry) {
                try {
                    val route = currentBackStackEntry?.toRoute<MediaNavRoutes.MediaViewerForLocal>()

                    if (route is MediaNavRoutes.MediaViewerForLocal) {
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.dark(Color.BLACK)
                        )
                    }
                } catch (_: Exception) {
                    enableEdgeToEdge()
                }
            }

            SetAuthChecker(isLoggedIn)

            LetThemCookTheme(theme) {
                when (isLoggedIn.value) {
                    true, false -> {
                        val startDestination = remember(isLoggedIn.value) {
                            if (isLoggedIn.value == true) FeedNavRoutes.Feed else AuthNavRoutes.Login
                        }

                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            addAuthRoutes(
                                navController = navController,
                                logInRoute = FeedNavRoutes.Feed
                            )
                            addProfileRoutes(
                                navController = navController,
                                logOutRoute = AuthNavRoutes.Login,
                                navBarRoutes = navBarRoutes,
                                onViewRecipe = { navController.navigate(RecipeNavRoutes.Recipe(it)) },
                                onViewMediaLocal = { viewMedia(navController, it) },
                                onViewMediaImage = { viewMedia(navController, it) }
                            )
                            addFeedRoutes(
                                navController = navController,
                                navBarRoutes = navBarRoutes
                            )
                            addRecipeRoutes(
                                navController = navController,
                                navBarRoutes = navBarRoutes,
                                editorRoute = EditorNavRoutes.Builder,
                                cookingRoute = EditorNavRoutes.Cooking,
                                onViewMedia = { viewMedia(navController, it) }
                            )
                            addEditorRoutes(
                                navController = navController,
                                onViewMedia = { viewMedia(navController, it) }
                            )
                            addMediaRoutes(
                                navController = navController
                            )
                        }
                    }
                    null -> {
                         LoadingScreen()
                    }
                }
            }
        }
    }

    private fun viewMedia(navController: NavController, file: File) {
        navController.navigate(MediaNavRoutes.MediaViewerForLocal(file))
    }

    private fun viewMedia(navController: NavController, bitmap: Bitmap) {
        navController.navigate(MediaNavRoutes.MediaViewerImage(bitmap.toBytes()))
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        createConfigurationContext(config)
    }

    @Composable
    private fun SetAuthChecker(isLoggedIn: MutableState<Boolean?>) {
        LaunchedEffect(Unit) {
            val rtResult = authManager.checkRefreshToken()

            if (rtResult == TokenCheckResult.OK) {
                isLoggedIn.value = true
                authManager.refreshTokens()
            } else {
                isLoggedIn.value = false
                authManager.forgetUserAndTokens()
            }
        }
    }
}