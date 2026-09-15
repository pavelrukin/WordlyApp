plugins {
    id("wordly.android.library")
}

android {
    namespace = "com.rukinpavel.wordlyapp.core.testing"
}

dependencies {
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.mockk)
    api(libs.turbine)
}
