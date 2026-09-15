plugins {
    id("wordly.android.feature")
}

android {
    namespace = "com.rukinpavel.wordlyapp.feature.settings.impl"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(project(":feature:settings:api"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    implementation(libs.billing.ktx)

    testImplementation(project(":core:testing"))
}
