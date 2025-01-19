package com.letthemcook.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.letthemcook.auth.ui.navigation.AuthNavRoutes
import com.letthemcook.auth.ui.navigation.addAuthRoutes
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
        enableEdgeToEdge()

        val themeStateProvider by inject<ThemeStateProvider>()
        val languageStateProvider by inject<LanguageStateProvider>()

        setContent {
            val navController = rememberNavController()
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

            LetThemCookTheme(theme) {
                NavHost(
                    navController = navController,
//                    startDestination = AuthNavRoutes.Login
                    startDestination = EditorNavRoutes.Builder
                ) {
                    addAuthRoutes(
                        navController = navController,
                        logInRoute = FeedNavRoutes.Feed
                    )
                    addProfileRoutes(
                        navController = navController,
                        logOutRoute = AuthNavRoutes.Login,
                        navBarRoutes = navBarRoutes
                    )
                    addFeedRoutes(
                        navController = navController,
                        navBarRoutes = navBarRoutes
                    )
                    addRecipeRoutes(
                        navController = navController,
                        navBarRoutes = navBarRoutes,
                        editorRoute = EditorNavRoutes.Builder,
                        cookingRoute = EditorNavRoutes.Cooking
                    )
                    addEditorRoutes(
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