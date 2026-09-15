plugins {
    id("wordly.android.library")
    id("wordly.android.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.rukinpavel.wordlyapp.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)
    api(libs.kotlinx.serialization.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
