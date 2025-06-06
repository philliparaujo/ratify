import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.ratify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ratify"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val spotifyRedirectUri: String by project
        val redirectUri = URI(spotifyRedirectUri)

        manifestPlaceholders["redirectSchemeName"] = redirectUri.scheme
        manifestPlaceholders["redirectHostName"] = redirectUri.host
        manifestPlaceholders["redirectPathPrefix"] = redirectUri.path

        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"${project.findProperty("spotifyClientId")}\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"${project.findProperty("spotifyRedirectUri")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
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
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.graphics.shapes.android)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.11.0")

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Spotify
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    implementation(files("libs/spotify-auth-release-2.1.0.aar"))
    implementation(libs.gson)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.appcompat)

    // Fetching and loading network images
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Settings
    implementation(libs.androidx.datastore.preferences)

    // Dependency Injection
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.core)
}