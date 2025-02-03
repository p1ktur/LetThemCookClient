package com.letthemcook.core.domain.koin

import com.letthemcook.core.data.remote.authorization.AuthManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.local.files.FileDatabase
import com.letthemcook.core.data.local.LocalDataDatabase
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.remote.file.RemoteFileManager
import com.letthemcook.core.data.remote.user.UserManager
import com.letthemcook.core.domain.media.MediaFilePickerManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinCoreModule = module {
    // Local
    single { FileDatabase.getInstance(androidContext()) }
    single { get<FileDatabase>().getDao() }
    single { LocalFileManager(androidContext(), get()) }

    single { LocalDataDatabase.getInstance(androidContext()) }
    single { get<LocalDataDatabase>().getFavoredRecipeDao() }
    single { get<LocalDataDatabase>().getRecipeJsonDao() }
    single { get<LocalDataDatabase>().getRecipeReactionDao() }
    single { get<LocalDataDatabase>().getReviewLikeDao() }
    single { get<LocalDataDatabase>().getFollowingDao() }
    single { LocalDataManager(get(), get(), get(), get(), get()) }

    single { MediaFilePickerManager(androidContext(), get()) }

    // Remote
    single { AuthManager(androidContext()) }
    single { UserManager(get()) }
    single { RemoteFileManager(get()) }
}