package com.letthemcook.core.domain.koin

import com.letthemcook.core.data.authorization.AuthManager
import com.letthemcook.core.data.files.FilesManager
import com.letthemcook.core.data.files.FileDatabase
import com.letthemcook.core.data.local.LocalDataDatabase
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.domain.media.MediaFilePickerManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinCoreModule = module {
    // Local
    single { FileDatabase.getInstance(androidContext()) }
    single { get<FileDatabase>().getDao() }
    single { FilesManager(androidContext(), get()) }

    single { LocalDataDatabase.getInstance(androidContext()) }
    single { get<LocalDataDatabase>().getFavoredRecipeDao() }
    single { get<LocalDataDatabase>().getRecipeJsonDao() }
    single { get<LocalDataDatabase>().getRecipeReactionDao() }
    single { get<LocalDataDatabase>().getReviewLikeDao() }
    single { get<LocalDataDatabase>().getFollowingDao() }
    single { LocalDataManager(get(), get(), get(), get(), get()) }

    single { MediaFilePickerManager(androidContext(), get()) }

    // Remove
    single { AuthManager(androidContext()) }
}