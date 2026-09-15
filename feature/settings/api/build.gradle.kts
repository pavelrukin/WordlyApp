plugins {
    id("wordly.android.library")
}

android {
    namespace = "com.rukinpavel.wordlyapp.feature.settings.api"
}

dependencies {
    implementation(project(":core:navigation"))
}
