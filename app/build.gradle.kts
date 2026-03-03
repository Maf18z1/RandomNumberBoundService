// Импортируем нужные классы для Kotlin compiler options
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.randomnumberboundservice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.randomnumberboundservice"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.3.0" // Соответствует Kotlin 2.3.10
    }

    // Настройки Java (только для Java-кода)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// --- Настройки Kotlin компилятора (ОТДЕЛЬНЫЙ БЛОК на том же уровне, что и android) ---
kotlin {
    compilerOptions {
        // Указываем целевую версию JVM для Kotlin (должна совпадать с JavaVersion.VERSION_17)
        jvmTarget = JvmTarget.fromTarget("17")

        // Если вы используете opt-in-аннотации, добавьте:
        // optIn.add("kotlin.RequiresOptIn")
    }
}

dependencies {
    // --- Compose BOM (актуальная версия) ---
    implementation(platform("androidx.compose:compose-bom:2025.04.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // --- Сериализация ---
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    // --- Для чтения JSON из assets (Gson или kotlinx, на ваш выбор) ---
    implementation("com.google.code.gson:gson:2.11.0")

    // --- Корутины ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // --- Lifecycle для Compose ---
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // --- Тесты ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.00"))

    // Material Components (нужно для XML-темы)
    implementation("com.google.android.material:material:1.12.0")
    // AppCompat (часто требуется вместе с material)
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("io.coil-kt:coil-compose:2.7.0")
}