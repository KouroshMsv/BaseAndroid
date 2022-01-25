plugins {
    id("com.android.application")
    kotlin("android")
}

val kotlinVersion: String by project
val minSdkVer: String  by project
val targetSdkVer: String  by project
val compileSdkVer: String   by project
val buildToolsVer: String  by project
val coroutines: String by project
val appcompat: String by project
val sourceCompatibilityVersion: String by project

android {
    compileSdk = 32
    buildToolsVersion = "32.0.0"
    defaultConfig {
        minSdk=21
        targetSdk=32
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility=JavaVersion.VERSION_1_8
        targetCompatibility=JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation(project(":basedomain"))
    implementation(project(":baseapp"))
    implementation(project(":accountmanager"))

    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
}
