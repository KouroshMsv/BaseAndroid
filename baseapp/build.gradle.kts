plugins {
    id("com.android.library")
    kotlin("android")

}

val kotlinVersion: String by project
val minSdkVer: String  by project
val targetSdkVer: String  by project
val compileSdkVer: String   by project
val buildToolsVer: String  by project
val coroutines: String by project
val appcompat: String by project
val liveData: String by project
val sourceCompatibilityVersion: String by project
group = "com.github.KouroshMsv"

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



        buildFeatures {
            viewBinding = true
            dataBinding = true
        }
        kotlinOptions {
            jvmTarget ="1.8"
        }
    }
}
dependencies {
    implementation((fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("androidx.appcompat:appcompat:${appcompat}")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.paging:paging-runtime-ktx:3.1.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${liveData}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${liveData}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${liveData}")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${liveData}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${liveData}")

    implementation("com.google.android.material:material:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")
    implementation("io.github.inflationx:calligraphy3:3.1.1")
    implementation("io.github.inflationx:viewpump:2.0.3")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation(project(":basedomain"))

}