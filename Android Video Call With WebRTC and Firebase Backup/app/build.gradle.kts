plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.androidvideocallwithwebrtcandfirebasebackup"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.androidvideocallwithwebrtcandfirebasebackup"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt Bağımlılıkları
    implementation("com.google.dagger:hilt-android:2.51")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.0")
    kapt("com.google.dagger:hilt-compiler:2.51")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Diğer Bağımlılıklar
    implementation("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.guolindev.permissionx:permissionx:1.7.1")

    //implementation("io.antmedia:webrtc-android-framework:2.8.0-SNAPSHOT")

    //implementation("org.webrtc:google-webrtc:1.0.24064")
    implementation("com.mesibo.api:webrtc:1.0.5")
    //implementation("io.antmedia:webrtc-android-framework:2.8.0-SNAPSHOT")
    //implementation("com.dafruits:webrtc:123.0.0")
}

kapt {
    correctErrorTypes = true
}