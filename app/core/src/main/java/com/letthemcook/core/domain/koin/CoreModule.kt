package com.letthemcook.core.domain.koin

import com.letthemcook.core.data.files.FilesManager
import com.letthemcook.core.data.files.database.FileDatabase
import com.letthemcook.core.domain.media.MediaFilePickerManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinCoreModule = module {
    single { FileDatabase.getInstance(androidContext()) }
    single { get<FileDatabase>().getDao() }
    single { FilesManager(androidContext(), get()) }
    single { MediaFilePickerManager(androidContext(), get()) }
}