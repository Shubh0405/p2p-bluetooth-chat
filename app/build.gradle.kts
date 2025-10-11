plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.p2p_bluetooth_chat"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.p2p_bluetooth_chat"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material
    // https://mvnrepository.com/artifact/com.google.android.material/material
    implementation("com.google.android.material:material:1.13.0")

    val nav_version = "2.9.5"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Compose UI
    // https://mvnrepository.com/artifact/androidx.compose.foundation/foundation
    implementation("androidx.compose.foundation:foundation:1.9.1")

    // Accompanist for permissions
    // https://mvnrepository.com/artifact/com.google.accompanist/accompanist-permissions
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    // Hilt for dependency injection
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-android
    implementation("com.google.dagger:hilt-android:2.57.2")
    // https://mvnrepository.com/artifact/androidx.hilt/hilt-navigation-compose
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    kapt("com.google.dagger:hilt-compiler:2.57.2")
}