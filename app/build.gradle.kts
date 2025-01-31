plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.androidKotlin)
    alias(libs.plugins.androidComposeCompiler)
    alias(libs.plugins.serializationJson)
}

android {
    namespace = "com.letthemcook.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.letthemcook.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Modules
    implementation(project(":app:core"))
    implementation(project(":app:theme"))
    implementation(project(":app:feature_auth"))
    implementation(project(":app:feature_profile"))
    implementation(project(":app:feature_feed"))
    implementation(project(":app:feature_recipe"))
    implementation(project(":app:feature_editor"))
    implementation(project(":app:feature_media"))

    // Common
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.icons)

    // Icons
    implementation(libs.androidx.material.icons.extended.android)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Test
    implementation(libs.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.junit)
}