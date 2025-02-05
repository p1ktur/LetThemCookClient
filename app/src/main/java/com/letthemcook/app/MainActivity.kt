package com.letthemcook.app

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cooking.media.ui.host.MediaNavRoutes
import com.letthemcook.theme.ui.navigation.MediaViewerAccess
import com.cooking.media.ui.host.MediaViewerHost
import com.letthemcook.auth.ui.navigation.AuthNavRoutes
import com.letthemcook.auth.ui.navigation.addAuthRoutes
import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.domain.model.auth.tokens.TokenCheckResult
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
import com.letthemcook.theme.screensContainer.ScreensContainer
import com.letthemcook.theme.ui.navigation.CookingRoutes
import com.letthemcook.theme.ui.navigation.NavBarRoutes
import com.letthemcook.theme.ui.screens.LoadingScreen
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private val navBarRoutes = NavBarRoutes(
        navigateToHome = { navController.navigate(FeedNavRoutes.Feed) },
        navigateToNewRecipe = { navController.navigate(RecipeNavRoutes.EditedRecipe(null)) },
        navigateToProfile = { navController.navigate(ProfileNavRoutes.EditedProfile) }
    )

    private val cookingRoutes = CookingRoutes(
        navigateToEditor = { navController.navigate(EditorNavRoutes.Builder(it)) },
        navigateToCooking = { navController.navigate(EditorNavRoutes.Cooking(it)) }
    )

    private val authManager by inject<AuthManager>()

    //TODO move media picker dialog onto screenContainer
    //TODO limits on blocks and products and categories
    //TODO check file sizes upon choosing them
    //TODO check internet connection on start and allow offline usage?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeStateProvider by inject<ThemeStateProvider>()
        val languageStateProvider by inject<LanguageStateProvider>()

        setContent {
            val isLoggedIn = remember { mutableStateOf<Boolean?>(null) }

            navController = rememberNavController()
            val currentBackStackEntry by navController.currentBackStackEntryAsState()

            val theme by themeStateProvider.getTheme().collectAsState(Theme.LIGHT)
            val language by languageStateProvider.getLanguage().collectAsState(Language.ENGLISH)

            val mediaViewerAccess = remember { mutableStateOf(MediaViewerAccess.Dummy) }

            LaunchedEffect(language) {
                setLocale(
                    when (language) {
                        Language.ENGLISH -> "us"
                        Language.UKRAINIAN -> "ua"
                    }
                )
            }

            LaunchedEffect(currentBackStackEntry) {
                mediaViewerAccess.value.stopViewing()

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

                        ScreensContainer {
                            NavHost(
                                navController = navController,
                                startDestination = startDestination,
                                enterTransition = { EnterTransition.None },
                                popEnterTransition = { EnterTransition.None },
                                exitTransition = { ExitTransition.None },
                                popExitTransition = { ExitTransition.None }
                            ) {
                                addAuthRoutes(
                                    navController = navController,
                                    logInRoute = FeedNavRoutes.Feed
                                )
                                addProfileRoutes(
                                    navController = navController,
                                    logOutRoute = AuthNavRoutes.Login,
                                    navBarRoutes = navBarRoutes,
                                    mediaViewerAccessState = mediaViewerAccess,
                                    navigateToRecipe = ::navigateToRecipe
                                )
                                addFeedRoutes(
                                    navController = navController,
                                    navBarRoutes = navBarRoutes,
                                    navigateToProfile = ::navigateToProfile,
                                    navigateToRecipe = ::navigateToRecipe
                                )
                                addRecipeRoutes(
                                    navController = navController,
                                    navBarRoutes = navBarRoutes,
                                    cookingRoutes = cookingRoutes,
                                    mediaViewerAccessState = mediaViewerAccess,
                                    navigateToProfile = ::navigateToProfile
                                )
                                addEditorRoutes(
                                    navController = navController,
                                    mediaViewerAccessState = mediaViewerAccess
                                )
                            }
                            MediaViewerHost(mediaViewerAccess)
                        }
                    }
                    null -> {
                         LoadingScreen()
                    }
                }
            }
        }
    }

    private fun navigateToRecipe(recipeId: String) {
        navController.navigate(RecipeNavRoutes.Recipe(recipeId))
    }

    private fun navigateToProfile(userId: String) {
        navController.navigate(ProfileNavRoutes.Profile(userId))
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