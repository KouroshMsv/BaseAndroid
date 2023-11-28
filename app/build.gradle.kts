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

android {
    compileSdk = compileSdkVer.toInt()
    buildToolsVersion = buildToolsVer
    defaultConfig {
        applicationId ="dev.kourosh.baseandroid"
        minSdk=minSdkVer.toInt()
        targetSdk=targetSdkVer.toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility=JavaVersion.VERSION_17
        targetCompatibility=JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    namespace = "dev.kourosh.baseandroid"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(":basedomain"))
    implementation(project(":baseapp"))
    implementation(project(":accountmanager"))

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
}
