plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "mx.edu.utez.musicp"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.edu.utez.musicp"
        minSdk = 27
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.material3)
    implementation(libs.espresso.core)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.media3.exoplayer)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("androidx.media3:media3-session:1.3.1")

    implementation(libs.coil.compose)
    // AGREGAR ESTAS DEPENDENCIAS PARA LOS ICONOS
    implementation("androidx.compose.material:material-icons-core:1.6.3")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

    val roomVersion = "2.6.1" // O la versión que estés usando

    // 1. Dependencia principal de Room
    implementation("androidx.room:room-runtime:$roomVersion")

    // 2. Room con soporte para Coroutines y Flow
    implementation("androidx.room:room-ktx:$roomVersion")

    // 3. 🚨 ¡ESTA ES LA CLAVE! El Procesador de Anotaciones
    // Si usas Kotlin Annotation Processing Tool (KAPT)
    // kapt("androidx.room:room-compiler:$roomVersion")

    // Si usas Kotlin Symbol Processing (KSP) [Recomendado]
    // Debes añadir el plugin KSP arriba y usar esta línea:
    ksp("androidx.room:room-compiler:$roomVersion")


}