import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.getByType(CommonExtension::class.java)
            extension.buildFeatures.compose = true

            dependencies {
                add("implementation", platform("androidx.compose:compose-bom:2026.09.00"))
                add("androidTestImplementation", platform("androidx.compose:compose-bom:2026.09.00"))
            }
        }
    }
}
