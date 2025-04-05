import dev.kikugie.stonecutter.build.StonecutterBuild
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

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

/**
 * Retrieves a version-specific or wildcard property key from the project's properties.
 *
 * @param key the specific key to identify the property related to the version or a wildcard.
 * @return the value of the property as a string if found, or null if the property is missing.
 */
fun Project.versionPropOrNull(key: String): String? {
  val specificKey = "vers.${stonecutter(project).current.version.replace(".", "_")}.$key"
  val wildcardKey = "vers.*.$key"

  return when {
    project.prop(specificKey) != null -> project.prop(specificKey)!!
    project.prop(wildcardKey) != null -> project.prop(wildcardKey)!!
    else -> null
  }
}

/**
 * Applies a set of properties to a `ProcessResources` task, based on the project's configuration
 * and additional resource files.
 *
 * @param project the Gradle project that contains the configuration and properties.
 * @param files an iterable collection of file paths to which the properties will be applied.
 */
fun ProcessResources.applyProperties(project: Project, files: Iterable<String>) {
  val props = mutableMapOf(
    "mod_version" to project.prop("mod.version"),
    "mod_group" to project.prop("mod.group"),
    "mod_id" to project.prop("mod.id"),

    "mod_name" to project.prop("mod.name"),
    "mod_description" to project.prop("mod.description"),
    "mod_license" to project.prop("mod.license"),

    "minecraft_version" to stonecutter(project).current.version,
  )

  fun addNullable(name: String, value: String?) {
    if (value != null) props[name] = value
  }

  addNullable("fabric_loader_version", project.versionPropOrNull("fabric_loader"))
  addNullable("forge_loader_version", project.versionPropOrNull("forge_loader"))
  addNullable("neoforge_loader_version", project.versionPropOrNull("neoforge_loader"))

  inputs.properties(props)
  filesMatching(files) {
    expand(props)
  }
}

fun stonecutter(project: Project): StonecutterBuild {
  return requireNotNull(project.extensions.findByType(StonecutterBuild::class.java)) { "Stonecutter build extension not found" }
}
