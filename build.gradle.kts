plugins {
    alias(libs.plugins.android-application) apply false
    alias(libs.plugins.kotlin-compose) apply false
    alias(libs.plugins.google-devtools-ksp) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}