plugins {
    id("wordly.android.feature")
}

android {
    namespace = "com.rukinpavel.wordlyapp.feature.settings.impl"
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val appVersionName = libs.versions.appVersionName.get()
        buildConfigField("String", "APP_VERSION_NAME", "\"$appVersionName\"")
    }
}

dependencies {
    api(project(":feature:settings:api"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    implementation(libs.billing.ktx)

    testImplementation(project(":core:testing"))
}
