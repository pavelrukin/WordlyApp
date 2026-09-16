import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("wordly.android.library")
                apply("wordly.android.hilt")
                apply("wordly.android.compose")
            }

            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:navigation"))
                add("implementation", project(":core:domain"))

                add("implementation", libs.findLibrary("compose-material3").get())
                add("implementation", libs.findLibrary("compose-ui").get())
                add("implementation", libs.findLibrary("compose-ui-graphics").get())
                add("implementation", libs.findLibrary("compose-ui-tooling-preview").get())
                add("implementation", libs.findLibrary("compose-material-icons-extended").get())

                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
            }
        }
    }
}
