plugins {
    id("wordly.kotlin.library")
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
