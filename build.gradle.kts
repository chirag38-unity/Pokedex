// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.6.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.0")
    }
}

plugins {
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.24" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
}

allprojects {
    repositories{
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}