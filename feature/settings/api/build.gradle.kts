plugins {
    id("wordly.android.library")
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.rukinpavel.wordlyapp.feature.settings.api"
}

dependencies {
    api(project(":core:navigation"))
    api(libs.kotlinx.serialization.core)
}
