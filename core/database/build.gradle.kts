plugins {
    id("wordly.android.library")
    id("wordly.android.hilt")
}

android {
    namespace = "com.rukinpavel.wordlyapp.core.database"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.datastore.preferences)
}
