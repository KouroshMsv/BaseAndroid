plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

group = "com.github.KouroshMsv"
afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class.java) {

                from(components.getByName("release"))
                groupId = "com.github.KouroshMsv"
                artifactId = "final"
                version = "1.9.9"
            }
        }
    }
}
val kotlinVersion: String by project
val minSdkVer: String by project
val compileSdkVer: String by project
val buildToolsVer: String by project
val targetSdkVer: String by project
val coroutines: String by project
val appcompat: String by project

android {
    compileSdk = 32
    buildToolsVersion = "32.0.0"
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation(project(":basedomain"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")
}
apply(plugin = "com.github.dcendents.android-maven")
