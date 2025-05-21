import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.dagger.hilt)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.aarevalo.tasky"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aarevalo.tasky"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
        buildConfigField("String", "API_BASE_URL", "\"${properties.getProperty("API_BASE_URL")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.dagger.compiler)

    // Hilt Navigation
    implementation(libs.hilt.navigation.compose)

    // Navigation Compose
    implementation(libs.jetbrains.compose.navigation)

    // Coil3
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.core)
    implementation(libs.coil.network.okhttp)

    // Moshi
    implementation(libs.moshi.kotlin)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Material Icons
    implementation(libs.material.icons)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Android Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // Data Store Preferences
    implementation(libs.androidx.datastore.preferences)
}