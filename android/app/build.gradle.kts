plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sportzfy.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sportzfy.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 7
        versionName = "7.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Pack native FFmpeg libs
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    // AndroidX core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Media3 / ExoPlayer — full streaming stack
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-exoplayer-rtsp:1.2.1")
    implementation("androidx.media3:media3-exoplayer-smoothstreaming:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-session:1.2.1")
    implementation("androidx.media3:media3-extractor:1.2.1")
    // FFmpeg decoder — extra codec support (VP8, VP9, FLAC, ALAC, etc.)
    implementation("androidx.media3:media3-decoder-ffmpeg:1.2.1")

    // Layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Glide — channel/team logo loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
