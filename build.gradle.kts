import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm") version "2.1.20"
  id("dev.isxander.modstitch.base") version "0.5.15+"
  id("dev.kikugie.stonecutter")
}

fun useDep(name: String, consumer: (prop: String) -> Unit): Unit? =
  (findProperty("deps.$name") as? String?)?.let(consumer)

fun getDep(name: String): String =
  findProperty("deps.$name") as String

val minecraft = getDep("minecraft")

fun atLeast(version: String): Boolean = stonecutter.eval(minecraft, ">=$version")

val java = when {
  atLeast("1.20.5") -> Pair(21, JvmTarget.JVM_21)
  atLeast("1.18") -> Pair(17, JvmTarget.JVM_17)
  atLeast("1.17") -> Pair(16, JvmTarget.JVM_16)
  else -> Pair(8, JvmTarget.JVM_1_8)
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

  parchment {
    useDep("parchment") { mappingsVersion = it }
  }

  metadata {
    modId = "drop_confirm"
    modName = "DropConfirm"
    modVersion = "4.1.0"
    modGroup = "xyz.pupbrained.drop_confirm"
    modAuthor = "pupbrained"

    replacementProperties.put("mod_issue_tracker", "https://github.com/pupbrained/drop-confirm/issues")
    replacementProperties.put(
      "pack_format",
      when (minecraft) {
        "1.14.4" -> "4"
        "1.15.2" -> "5"
        "1.16.5" -> "6"
        "1.17.1" -> "7"
        "1.18.2" -> "8"
        "1.19.4" -> "13"
        "1.20.1" -> "15"
        "1.20.4" -> "22"
        "1.20.6" -> "32"
        "1.21.1" -> "34"
        "1.21.3" -> "42"
        "1.21.4" -> "46"
        "1.21.5", "25w14craftmine" -> "55"
        "25w15a" -> "56"
        else -> throw IllegalArgumentException("Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
      }
    )
  }

  loom { fabricLoaderVersion = "0.16.13" }

  moddevgradle {
    defaultRuns()
    configureNeoforge { runs.all { disableIdeRun() } }

    enable {
      useDep("forge") { forgeVersion = it }
      useDep("neoforge") { neoForgeVersion = it }
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
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn("stonecutterGenerate")
  }

  named("compileKotlin") { dependsOn("stonecutterGenerate") }
  processResources { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
}

stonecutter {
  val loader = name.split("-")[1]

  consts(
    "fabric" to (loader == "fabric"),
    "neoforge" to (loader == "neoforge"),
    "forge" to (loader == "forge")
  )
}

dependencies {
  when {
    atLeast("25w14craftmine") -> modstitchModImplementation("dev.isxander:yet-another-config-lib:3.6.6+1.21.5-$loader")
    atLeast("1.20.1") && loader != "forge" -> modstitchModImplementation("dev.isxander:yet-another-config-lib:3.6.6+$minecraft-$loader")
    else -> modstitchModImplementation("maven.modrinth:unilib:1.0.5+$minecraft-$loader")
  }

  modstitch {
    loom {
      // TODO: Figure out how to just use the needed modules instead of including the entirety of fabric-api
      modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${getDep("fabric-api")}")

      if (minecraft != "25w15a")
        modstitchModImplementation(
          "maven.modrinth:modmenu:${
            when (minecraft) {
              "1.14.4" -> "1.7.17"
              "1.15.2" -> "1.10.7"
              "1.16.5" -> "1.16.23"
              "1.17.1" -> "2.0.17"
              "1.18.2" -> "3.2.5"
              "1.19.4" -> "6.3.1"
              "1.20.1" -> "7.2.2"
              "1.20.4" -> "9.2.0"
              "1.20.6" -> "10.0.0"
              "1.21.1" -> "11.0.3"
              "1.21.3" -> "12.0.0"
              "1.21.4" -> "13.0.3"
              "1.21.5", "25w14craftmine" -> "14.0.0-rc.2"
              else -> throw IllegalStateException("No modmenu version defined for $minecraft")
            }
          }"
        )
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
