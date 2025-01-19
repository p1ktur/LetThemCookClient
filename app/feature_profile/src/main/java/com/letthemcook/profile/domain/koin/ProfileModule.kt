package com.letthemcook.profile.domain.koin

import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeViewModel
import com.letthemcook.profile.domain.viewModels.profile.ProfileViewModel
import com.letthemcook.profile.domain.viewModels.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinProfileModule = module {
    viewModel { ProfileViewModel() }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { PasswordChangeViewModel() }
}