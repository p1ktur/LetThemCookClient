package com.letthemcook.theme.ui.navigation

import com.letthemcook.core.domain.model.remote.WeightedProduct

data class CookingRoutes(
    val navigateToEditor: (String, String, String?, String, List<WeightedProduct>) -> Unit,
    val navigateToCooking: (String, String, String, String) -> Unit,
    val navigateToTutorial: () -> Unit
)