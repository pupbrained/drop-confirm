import dev.kikugie.stonecutter.build.StonecutterBuild
import org.gradle.api.Project

/**
 * Retrieves a property value from the project based on the provided key.
 *
 * @param key the property key to look up in the project's properties.
 * @return the value of the property as a string, or null if the property does not exist.
 */
fun Project.prop(key: String): String? = findProperty(key)?.toString()

/**
 * Retrieves a version-specific or wildcard property key from the project's properties.
 *
 * @param key the specific key to identify the property related to the version or a wildcard.
 * @return the value of the property as a string if found, or throws an IllegalArgumentException if the property is missing.
 * @throws IllegalArgumentException if neither the version-specific key nor the wildcard key is found.
 */
fun Project.versionProp(key: String): String {
  val specificKey = "vers.${stonecutter(project).current.version.replace(".", "_")}.$key"
  val wildcardKey = "vers.*.$key"

  return when {
    project.prop(specificKey) != null -> project.prop(specificKey)!!
    project.prop(wildcardKey) != null -> project.prop(wildcardKey)!!
    else -> throw IllegalArgumentException("Missing '$specificKey' or '$wildcardKey'")
  }
}

fun stonecutter(project: Project): StonecutterBuild {
  return requireNotNull(project.extensions.findByType(StonecutterBuild::class.java)) { "Stonecutter build extension not found" }
}
