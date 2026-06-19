plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinCompose) apply false
    alias(libs.plugins.googleDevtoolsKsp) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}