package com.letthemcook.profile.domain.koin

import com.letthemcook.profile.domain.viewModels.passwordChange.PasswordChangeViewModel
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileViewModel
import com.letthemcook.profile.domain.viewModels.profile.ProfileViewModel
import com.letthemcook.profile.domain.viewModels.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinProfileModule = module {
    viewModel { EditedProfileViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { PasswordChangeViewModel(get(), get()) }
}