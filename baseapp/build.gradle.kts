plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")

}
afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {

                from(components.getByName("release"))
                groupId = "com.github.KouroshMsv"
                artifactId = "baseapp"
                version = libVersion
            }
        }
    }
}
val libVersion: String by project
val kotlinVersion: String by project
val minSdkVer: String  by project
val targetSdkVer: String  by project
val compileSdkVer: String   by project
val buildToolsVer: String  by project
val coroutines: String by project
val appcompat: String by project
val liveData: String by project
group = "com.github.KouroshMsv"

android {
    compileSdk = compileSdkVer.toInt()
    buildToolsVersion = buildToolsVer
    defaultConfig {
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
        sourceCompatibility=JavaVersion.VERSION_11
        targetCompatibility=JavaVersion.VERSION_11



        buildFeatures {
            viewBinding = true
            dataBinding = true
        }
        kotlinOptions {
            jvmTarget ="11"
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
    implementation( "androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.google.android.material:material:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")
    implementation("io.github.inflationx:calligraphy3:3.1.1")
    implementation("io.github.inflationx:viewpump:2.0.3")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation(project(":basedomain"))

}
