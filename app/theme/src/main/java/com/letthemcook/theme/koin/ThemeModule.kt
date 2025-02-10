package com.letthemcook.theme.koin

import com.letthemcook.theme.components.dialogs.bottom.mediaPickMethod.media.MediaFilePickerManager
import com.letthemcook.theme.providers.LanguageStateProvider
import com.letthemcook.theme.providers.ThemeStateProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinThemeModule = module {
    single { ThemeStateProvider(androidContext()) }
    single { LanguageStateProvider(androidContext()) }

    single { MediaFilePickerManager(androidContext(), get()) }
}