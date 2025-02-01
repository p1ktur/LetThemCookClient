package com.letthemcook.app

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cooking.media.ui.navigation.MediaNavRoutes
import com.cooking.media.ui.navigation.addMediaRoutes
import com.letthemcook.auth.ui.navigation.AuthNavRoutes
import com.letthemcook.auth.ui.navigation.addAuthRoutes
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
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val navBarRoutes = NavBarRoutes(
        homeRoute = FeedNavRoutes.Feed,
        addRoute = RecipeNavRoutes.EditedRecipe(null),
        profileRoute = ProfileNavRoutes.Profile,
    )

    //TODO limits on blocks and products and categories

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeStateProvider by inject<ThemeStateProvider>()
        val languageStateProvider by inject<LanguageStateProvider>()

        setContent {
            val navController = rememberNavController()
            val currentBackStackEntry by navController.currentBackStackEntryAsState()

            val theme by themeStateProvider.getTheme().collectAsState(Theme.LIGHT)
            val language by languageStateProvider.getLanguage().collectAsState(Language.ENGLISH)

            val onViewMedia = remember {
                fun (file: File) {
                    navController.navigate(MediaNavRoutes.MediaViewer(file))
                }
            }

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
                    val route = currentBackStackEntry?.toRoute<MediaNavRoutes.MediaViewer>()

                    if (route is MediaNavRoutes.MediaViewer) {
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.dark(Color.BLACK)
                        )
                    }
                } catch (_: Exception) {
                    enableEdgeToEdge()
                }
            }

            LetThemCookTheme(theme) {
                NavHost(
                    navController = navController,
                    startDestination = AuthNavRoutes.Login
                ) {
                    addAuthRoutes(
                        navController = navController,
                        logInRoute = FeedNavRoutes.Feed
                    )
                    addProfileRoutes(
                        navController = navController,
                        logOutRoute = AuthNavRoutes.Login,
                        navBarRoutes = navBarRoutes,
                        onViewMedia = onViewMedia
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
                        onViewMedia = onViewMedia
                    )
                    addEditorRoutes(
                        navController = navController,
                        onViewMedia = onViewMedia
                    )
                    addMediaRoutes(
                        navController = navController
                    )
                }
            }
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        createConfigurationContext(config)
    }
}