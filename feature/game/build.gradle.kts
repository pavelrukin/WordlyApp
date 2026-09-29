plugins {
    id("wordly.android.feature")
}

android {
    namespace = "com.rukinpavel.wordlyapp.feature.game"
}

dependencies {
    implementation(project(":feature:settings:api"))

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}
