plugins {
    id("wordly.android.library")
    id("wordly.android.hilt")
}

android {
    namespace = "com.rukinpavel.wordlyapp.core.data"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:platform:android"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
    implementation(libs.billing.ktx)
    implementation(libs.androidx.datastore.preferences)
}
