package com.letthemcook.theme.ui.navigation

data class NavBarRoutes(
    val navigateToHome: () -> Unit,
    val navigateToNewRecipe: () -> Unit,
    val navigateToProfile: () -> Unit
)
