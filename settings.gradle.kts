pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WordlyApp"
include(":app")
include(":core:ui")
include(":core:navigation")
include(":core:model")
include(":core:common")
include(":core:platform:android")
include(":core:data")
include(":core:testing")
include(":core:database")
include(":core:network")
include(":core:domain")
include(":feature:game")
include(":feature:settings:api")
include(":feature:settings:impl")
include(":feature:onboarding")
