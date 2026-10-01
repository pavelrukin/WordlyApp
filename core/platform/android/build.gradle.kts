plugins {
    id("wordly.android.library")
    id("wordly.android.hilt")
}

android {
    namespace = "com.rukinpavel.wordlyapp.core.platform.android"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(libs.javax.inject)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.work.runtime.ktx)
    api(libs.play.app.update)
}
