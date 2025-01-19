package com.letthemcook.app

import android.app.Application
import com.letthemcook.auth.domain.koin.koinAuthModule
import com.letthemcook.editor.domain.koin.koinEditorModule
import com.letthemcook.feed.domain.koin.koinFeedModule
import com.letthemcook.profile.domain.koin.koinProfileModule
import com.letthemcook.recipe.domain.koin.koinRecipeModule
import com.letthemcook.theme.koin.themeKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(
                themeKoinModule,
                koinAuthModule,
                koinProfileModule,
                koinFeedModule,
                koinRecipeModule,
                koinEditorModule
            )
        }
    }
}