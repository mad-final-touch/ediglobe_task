plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  id("com.google.devtools.ksp") version "2.0.21-1.0.26"
  alias(libs.plugins.google.gms.google.services)
}

android {
  namespace = "com.example.ediglobe_task"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.example.ediglobe_task"
    minSdk = 30
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    viewBinding = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
//  implementation(libs.firebase.auth)
//  implementation(libs.androidx.credentials)
//  implementation(libs.androidx.credentials.play.services.auth)
//  implementation(libs.googleid)
  implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
  implementation("com.firebaseui:firebase-ui-auth:9.0.0")
  implementation("com.squareup.retrofit2:retrofit:3.0.0")
  implementation("com.squareup.retrofit2:converter-gson:3.0.0")
//  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Optional for logging

  // Add the dependency for the Firebase Authentication library
  // When using the BoM, you don't specify versions in Firebase library dependencies
  implementation("com.google.firebase:firebase-auth")

  // Also add the dependencies for the Credential Manager libraries and specify their versions
  implementation("androidx.credentials:credentials:1.3.0")
  implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
  implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
  implementation(libs.firebase.crashlytics.buildtools)
  val room_version = "2.6.1"
  val lifecycle_version = "2.8.0"
  val retrofit_version = "2.9.0" // Common stable version
//  val okhttp_logging_interceptor_version = "4.12.0" // Compatible with Retrofit 2.9.x

  // Room
  implementation("androidx.room:room-runtime:$room_version")
  ksp("androidx.room:room-compiler:$room_version")
  implementation("androidx.room:room-ktx:$room_version") // For Kotlin Coroutines support

  // Lifecycle
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

  // Retrofit & Gson Converter
  implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
  implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")

  // OkHttp Logging Interceptor
//  implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_logging_interceptor_version")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}