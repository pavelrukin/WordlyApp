plugins {
    id("wordly.android.library")
    id("wordly.android.hilt")
}

android {
    namespace = "com.rukinpavel.wordlyapp.core.platform.android"
}

dependencies {
    implementation(libs.javax.inject)
}
