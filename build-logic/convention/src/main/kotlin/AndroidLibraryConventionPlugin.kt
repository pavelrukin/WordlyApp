import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                // org.jetbrains.kotlin.android is no longer required since AGP 9.0
            }

            extensions.configure<LibraryExtension> {
                configureAndroidBaseOptions(this)
            }

            dependencies {
                add("testImplementation", kotlin("test"))
            }
        }
    }
}
