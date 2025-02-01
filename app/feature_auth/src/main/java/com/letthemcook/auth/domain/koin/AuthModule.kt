package com.letthemcook.auth.domain.koin

import com.letthemcook.auth.domain.viewModels.login.LoginViewModel
import com.letthemcook.auth.domain.viewModels.registration.RegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val koinAuthModule = module {
    viewModel { RegistrationViewModel(get()) }
    viewModel { LoginViewModel(get()) }
}