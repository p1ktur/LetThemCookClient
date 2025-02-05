package com.letthemcook.theme.ui.navigation

data class CookingRoutes(
    val navigateToEditor: (String?) -> Unit,
    val navigateToCooking: (String) -> Unit
)