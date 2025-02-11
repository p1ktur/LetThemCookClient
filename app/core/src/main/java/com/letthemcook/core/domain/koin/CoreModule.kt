package com.letthemcook.core.domain.koin

import com.letthemcook.core.data.remote.AuthManager
import com.letthemcook.core.data.local.files.LocalFileManager
import com.letthemcook.core.data.local.files.FileDatabase
import com.letthemcook.core.data.local.LocalDataDatabase
import com.letthemcook.core.data.local.LocalDataManager
import com.letthemcook.core.data.remote.RecipeManager
import com.letthemcook.core.data.remote.RemoteFileManager
import com.letthemcook.core.data.remote.ReviewManager
import com.letthemcook.core.data.remote.UserManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinCoreModule = module {
    // Local
    single { FileDatabase.getInstance(androidContext()) }
    single { get<FileDatabase>().getDao() }
    single { LocalFileManager(androidContext(), get()) }

    single { LocalDataDatabase.getInstance(androidContext()) }
    single { get<LocalDataDatabase>().getRecipeDao() }
    single { get<LocalDataDatabase>().getRecipeReactionDao() }
    single { get<LocalDataDatabase>().getReviewLikeDao() }
    single { LocalDataManager(get(), get(), get(), get()) }

    // Remote
    single { AuthManager(androidContext()) }
    single { RecipeManager(get(), get(), get()) }
    single { RemoteFileManager(get()) }
    single { ReviewManager(get(), get(), get(), get()) }
    single { UserManager(get(), get()) }
}