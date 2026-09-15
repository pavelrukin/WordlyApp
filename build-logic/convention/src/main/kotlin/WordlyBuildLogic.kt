import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.configureAndroidBaseOptions(
    commonExtension: CommonExtension,
) {
    commonExtension.compileSdk = 35
    commonExtension.defaultConfig.minSdk = 24

    commonExtension.compileOptions.sourceCompatibility = JavaVersion.VERSION_17
    commonExtension.compileOptions.targetCompatibility = JavaVersion.VERSION_17
}
