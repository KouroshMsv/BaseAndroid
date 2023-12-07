buildscript {
    val kotlinVersion: String by project
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url =uri("https://jitpack.io") }

    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
