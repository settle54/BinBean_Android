import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

val localProperties = Properties().apply {
    FileInputStream(rootProject.file("local.properties")).use {
        load(it)
    }
}

android {
    namespace = "com.binbean.retrofit"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "KAKAO_REST_API_KEY",
            "\"${localProperties.getProperty("KAKAO_REST_API_KEY")}\""
        )
        buildConfigField(
            "String",
            "KAKAO_BASE_URL",
            "\"${localProperties.getProperty("KAKAO_BASE_URL")}\""
        )
        buildConfigField(
            "String",
            "SERVICE_BASE_URL",
            "\"${localProperties.getProperty("SERVICE_BASE_URL")}\""
        )
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
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit.converter)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}