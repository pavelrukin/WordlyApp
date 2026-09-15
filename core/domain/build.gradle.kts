plugins {
    id("wordly.kotlin.library")
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}
