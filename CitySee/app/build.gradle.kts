import com.android.tools.build.jetifier.core.utils.Log
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {

    val localProperties = Properties()
    localProperties.load(FileInputStream(rootProject.file("local.properties")))

    android.buildFeatures.buildConfig = true

    namespace = "hu.bme.aut.citysee"
    compileSdk = 34

    defaultConfig {
        applicationId = "hu.bme.aut.citysee"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "MAPS_API_KEY",  "\"${localProperties["MAPS_API_KEY"]}\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "MAPS_API_KEY",  "\"${localProperties["MAPS_API_KEY"]}\"")
        }
        release {
            buildConfigField("String", "MAPS_API_KEY",  "\"${localProperties["MAPS_API_KEY"]}\"")
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    implementation("com.google.firebase:firebase-storage:21.0.1") // storgae for the images
    implementation("androidx.activity:activity-ktx:1.4.0")  // For the activity result APIs
    implementation("io.coil-kt:coil-compose:2.1.0")         // For loading images

    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation(libs.play.services.maps)
    // KTX for the Maps SDK for Android library
    implementation(libs.maps.ktx)
    // KTX for the Maps SDK for Android Utility Library
    implementation(libs.maps.utils.ktx)

    // Google Maps Compose library
    val mapsComposeVersion = "4.4.1"
    implementation(libs.maps.compose)
    // Google Maps Compose utility library
    implementation(libs.maps.compose.utils)
    // Google Maps Compose widgets library
    implementation(libs.maps.compose.widgets)

    // OkHttp for network requests
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    implementation (libs.gson)
}