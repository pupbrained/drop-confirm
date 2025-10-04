import io.github.klahap.dotenv.DotEnvBuilder
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.file.DuplicatesStrategy.INCLUDE
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_16
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import java.util.*

plugins {
  kotlin("jvm") version "2.1.21"
  id("me.modmuss50.mod-publish-plugin") version "0.8.4"
  id("dev.isxander.modstitch.base") version "0.5.16+"
  id("io.github.klahap.dotenv") version "1.1.3"
}

fun getDep(name: String): String =
  findProperty("deps.$name") as? String? ?: throw IllegalStateException("$name is not defined for $minecraft-$loader")

fun getDepOrNull(name: String): String? =
  findProperty("deps.$name") as? String?

val minecraft = getDep("minecraft")

fun atLeast(version: String): Boolean = stonecutter.eval(minecraft, ">=$version")

val java = when {
  atLeast("1.20.5") -> 21 to JVM_21
  atLeast("1.18") -> 17 to JVM_17
  atLeast("1.17") -> 16 to JVM_16
  else -> 8 to JVM_1_8
}

val loader = when {
  modstitch.isLoom -> "fabric"
  modstitch.isModDevGradleRegular -> "neoforge"
  modstitch.isModDevGradleLegacy -> "forge"
  else -> throw IllegalStateException("Unsupported loader")
}

modstitch {
  minecraftVersion = minecraft
  javaTarget = java.first
  parchment.mappingsVersion = getDepOrNull("parchment")

  metadata {
    modId = "drop_confirm"
    modName = "DropConfirm"
    modVersion = "5.0.2"
    modGroup = "xyz.pupbrained.drop_confirm"
    modAuthor = "pupbrained"
    modDescription = "Think twice before you drop. Adds a confirmation prompt when dropping items."
    modLicense = "MIT"

    replacementProperties.put("java", "${java.first}")

    if (loader == "fabric")
      replacementProperties.put("fabric_api", getDep("fabric-api"))

    replacementProperties.put(
      "loader_version", when (loader) {
        "fabric" -> "0.16.0"
        else -> getDep(loader)
      }
    )

    replacementProperties.put(
      "config_lib",
      when {
        atLeast("1.20.1") && loader != "forge" -> "yet_another_config_lib_v3"
        else -> "unilib"
      }
    )

    replacementProperties.put(
      "config_lib_version",
      when {
        atLeast("1.21.1") -> "3.7.1"
        atLeast("1.20.1") && loader != "forge" -> "3.6.6"
        else -> "1.1.0"
      }
    )

    replacementProperties.put("mod_issue_tracker", "https://github.com/pupbrained/drop-confirm/issues")
    replacementProperties.put("pack_format", getDep("pack_format"))
  }

  loom { fabricLoaderVersion = "0.16.14" }

  moddevgradle {
    defaultRuns()
    configureNeoforge { runs.all { disableIdeRun() } }

    enable {
      listOf(
        "forge" to ::forgeVersion,
        "neoforge" to ::neoForgeVersion
      ).forEach { (name, setter) -> (findProperty("deps.$name") as? String?)?.let { setter.set(it) } }
    }
  }

  mixin {
    addMixinsToModManifest = true
    configs.register("drop_confirm")
  }

  kotlin {
    jvmToolchain(java.first)
    compilerOptions.jvmTarget.set(java.second)
  }
}

tasks {
  named<ProcessResources>("generateModMetadata") {
    duplicatesStrategy = INCLUDE
    dependsOn("stonecutterGenerate")
  }

  named("compileKotlin") { dependsOn("stonecutterGenerate") }
  processResources { duplicatesStrategy = INCLUDE }
}

stonecutter {
  constants {
    val loader: String = current.project.substringAfter('-')
    match(loader, "fabric", "forge", "neoforge")
  }
}

dependencies {
  modstitchModImplementation(
    when {
      atLeast("1.21.9") -> "dev.isxander:yet-another-config-lib:3.8.0+1.21.9-$loader"
      atLeast("1.21.7") -> "dev.isxander:yet-another-config-lib:3.7.1+1.21.6-$loader"
      atLeast("1.21.1") -> "dev.isxander:yet-another-config-lib:3.7.1+$minecraft-$loader"
      atLeast("1.20.1") && loader != "forge" -> "dev.isxander:yet-another-config-lib:3.6.6+$minecraft-$loader"
      else -> "com.gitlab.cdagaming.unilib:UniLib-${loader.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}:1.1.0+$minecraft:$loader"
    }
  )

  if (!atLeast("1.20.1") || loader == "forge") {
    modstitchImplementation("org.quiltmc.parsers:json:0.3.1")
    modstitchJiJ("org.quiltmc.parsers:json:0.3.1")
  }

  modstitch {
    loom {
      listOf(
        "fabric-api-base",
        "fabric-lifecycle-events-v1",
        "fabric-key-binding-api-v1",
        "fabric-resource-loader-v0"
      ).forEach {
        modstitchModImplementation(
          (project.extensions.findByName("fabricApi") as FabricApiExtension).module(
            it,
            getDep("fabric-api")
          )
        )
      }

      modstitchModImplementation("maven.modrinth:modmenu:${getDep("modmenu")}")
    }

    moddevgradle {
      if (loader == "neoforge") {
        modstitchImplementation("thedarkcolour:kotlinforforge-neoforge:${if (atLeast("1.20.5")) "5.8.0" else "4.10.0"}")
      } else {
        modstitchModImplementation("thedarkcolour:kotlinforforge:${if (atLeast("1.19.4")) "4.10.0" else "3.12.0"}")
        modstitchImplementation("org.spongepowered:mixin:0.8.5")
        annotationProcessor("org.spongepowered:mixin:0.8.5")
      }
    }
  }
}

publishMods {
  val envFilePath = rootDir.resolve(".env")

  val envVars = DotEnvBuilder.dotEnv {
    addSystemEnv()

    if (envFilePath.exists() && envFilePath.isFile)
      addFile(envFilePath.absolutePath)
  }

  val supportedVersions = getDepOrNull("mcRange") ?: getDep("minecraft")

  val supportedVersionsList = supportedVersions.split(',')
    .map { it.trim() }
    .filter { it.isNotEmpty() }

  val versionRangeString: String = when {
    supportedVersionsList.isEmpty() -> ""
    supportedVersionsList.size == 1 -> supportedVersionsList.first()
    else -> "${supportedVersionsList.first()}-${supportedVersionsList.last()}"
  }

  val displayVersionSuffix = if (versionRangeString.isNotEmpty()) " ($versionRangeString)" else ""
  val releaseDisplayName =
    "${modstitch.metadata.modName.get()} ${modstitch.metadata.modVersion.get()}$displayVersionSuffix"

  type = STABLE
  file = modstitch.finalJarTask.get().archiveFile
  displayName = releaseDisplayName

  changelog = """
    This update fixes various issues with the mod's metadata.

    ## Dependencies

    ### Required
      * ${
    when {
      atLeast("1.21.1") && loader != "forge" -> "YetAnotherConfigLib `v3.7.1` or newer."
      atLeast("1.20.1") && loader != "forge" -> "YetAnotherConfigLib `v3.6.6`."
      else -> "UniLib `v1.1.0` or newer."
    }
  }
      * ${
    if (loader == "fabric") "Fabric Language Kotlin `v1.13.3` or newer." else when {
      atLeast("1.20.5") -> "Kotlin for Forge (NeoForge) `v5.8.0` or newer."
      atLeast("1.19.4") -> "Kotlin for Forge `v4.10.0`."
      else -> "Kotlin for Forge `v3.12.0`."
    }
  }

      ${
    if (loader == "fabric") """
    ### Recommended
      * ModMenu `v${getDep("modmenu")}` or newer.
    """ else ""
  }
  """.trimIndent()

  modLoaders.add(loader)

  github("github") {
    accessToken.set(envVars["GITHUB_TOKEN"])
    repository.set("pupbrained/drop-confirm")
    commitish.set("master")
    tagName.set("v${modstitch.metadata.modVersion.get()}-$minecraft-$loader")
  }

  curseforge("curseforge") {
    accessToken.set(envVars["CURSEFORGE_TOKEN"])
    projectId.set("881314")
    minecraftVersions.addAll(supportedVersionsList)

    requires(
      when {
        atLeast("1.20.1") && loader != "forge" -> "yacl"
        else -> "unilib"
      }
    )
    if (loader == "fabric") {
      requires("fabric-api")
      requires("fabric-language-kotlin")
      optional("modmenu")
    } else {
      requires("kotlin-for-forge")
    }
  }

  modrinth("modrinth") {
    accessToken.set(envVars["MODRINTH_TOKEN"])
    projectId.set("I45rjF2F")
    minecraftVersions.addAll(supportedVersionsList)

    requires(
      when {
        atLeast("1.20.1") && loader != "forge" -> "yacl"
        else -> "unilib"
      }
    )
    if (loader == "fabric") {
      requires("fabric-api")
      requires("fabric-language-kotlin")
      optional("modmenu")
    } else {
      requires("kotlin-for-forge")
    }
  }
}
