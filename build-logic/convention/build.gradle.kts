import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.rukinpavel.wordlyapp.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "wordly.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "wordly.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "wordly.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "wordly.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "wordly.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
    }
}
