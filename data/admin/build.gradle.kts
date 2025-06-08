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
    namespace = "com.binbean.admin"
    compileSdk = 35

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "ADMIN_API_TOKEN",
            "\"${localProperties.getProperty("ADMIN_API_TOKEN")}\""
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

    implementation(project(":domain"))
    implementation(project(":core:util"))
    implementation(project(":core:retrofit"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit.converter)
}