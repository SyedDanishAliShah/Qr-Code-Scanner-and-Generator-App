plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.qrcode"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.qrcode"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation ("androidx.camera:camera-camera2:1.4.0-alpha04")

// CameraX lifecycle library
    implementation ("androidx.camera:camera-lifecycle:1.4.0-alpha04")

// CameraX view class
    implementation ("androidx.camera:camera-view:1.4.0-alpha04")

// CameraX extensions library
    implementation ("androidx.camera:camera-extensions:1.4.0-alpha04")

    implementation ("com.airbnb.android:lottie:6.3.0")

    // ZXing Core library
    implementation ("com.google.zxing:core:3.4.1")

    // ZXing Android Embedded library
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")

    implementation ("androidx.room:room-runtime:2.6.1")

    kapt ("androidx.room:room-compiler:2.6.1")

    implementation ("androidx.room:room-ktx:2.6.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("com.google.android.gms:play-services-ads:21.5.0")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

}

