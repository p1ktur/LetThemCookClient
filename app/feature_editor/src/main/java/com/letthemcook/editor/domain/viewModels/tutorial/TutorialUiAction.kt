package com.letthemcook.editor.domain.viewModels.tutorial

sealed interface TutorialUiAction {
    data object NavigateBack : TutorialUiAction
}