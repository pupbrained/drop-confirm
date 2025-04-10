import org.gradle.api.file.DuplicatesStrategy.INCLUDE
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_16
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8

plugins {
  kotlin("jvm") version "2.1.20"
  id("dev.isxander.modstitch.base") version "0.5.15+"
  id("dev.kikugie.stonecutter")
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
    modVersion = "4.1.0"
    modGroup = "xyz.pupbrained.drop_confirm"
    modAuthor = "pupbrained"

    replacementProperties.put("mod_issue_tracker", "https://github.com/pupbrained/drop-confirm/issues")
    replacementProperties.put("pack_format", getDep("pack_format"))
  }

  loom { fabricLoaderVersion = "0.16.13" }

  moddevgradle {
    defaultRuns()
    configureNeoforge { runs.all { disableIdeRun() } }

    enable {
      listOf(
        "forge" to ::forgeVersion,
        "neoform" to ::neoFormVersion,
        "neoforge" to ::neoForgeVersion,
        "mcp" to ::mcpVersion
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

stonecutter.consts(
  "fabric" to (loader == "fabric"),
  "neoforge" to (loader == "neoforge"),
  "forge" to (loader == "forge")
)

dependencies {
  modstitchModImplementation(
    when {
      atLeast("25w14craftmine") -> "dev.isxander:yet-another-config-lib:3.6.6+1.21.5-$loader"
      atLeast("1.20.1") && loader != "forge" -> "dev.isxander:yet-another-config-lib:3.6.6+$minecraft-$loader"
      else -> "maven.modrinth:unilib:1.0.5+$minecraft-$loader"
    }
  )

  modstitch {
    loom {
      // TODO: Figure out how to just use the needed modules instead of including the entirety of fabric-api
      modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${getDep("fabric-api")}")

      if (minecraft != "25w15a")
        modstitchModImplementation("maven.modrinth:modmenu:${getDep("modmenu")}")
    }

    moddevgradle {
      if (loader == "neoforge") {
        modstitchImplementation("thedarkcolour:kotlinforforge-neoforge:${if (atLeast("1.20.5")) "5.6.0" else "4.10.0"}")
      } else {
        modstitchModImplementation("thedarkcolour:kotlinforforge:${if (atLeast("1.19.4")) "4.10.0" else "3.12.0"}")
        modstitchImplementation("org.spongepowered:mixin:0.8.5")
        annotationProcessor("org.spongepowered:mixin:0.8.5")
      }
    }
  }
}
