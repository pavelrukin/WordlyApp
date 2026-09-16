plugins {
    id("wordly.kotlin.library")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
    implementation(libs.hilt.core)
    ksp(libs.hilt.compiler)
}
