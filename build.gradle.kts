import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("dev.isxander.modstitch.base") version "0.5.14+"
  kotlin("jvm") version "2.1.20"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
  (findProperty(name) as? String?)
    ?.let(consumer)
}

val minecraft = property("deps.minecraft") as String

val java =
  if (stonecutter.eval(minecraft, ">=1.20.5")) Pair(21, JvmTarget.JVM_21)
  else Pair(17, JvmTarget.JVM_17)

val loader = when {
  modstitch.isLoom -> "fabric"
  modstitch.isModDevGradleRegular -> "neoforge"
  else -> throw IllegalStateException("Unsupported loader")
}

modstitch {
  minecraftVersion = minecraft

  javaTarget = java.first

  parchment {
    prop("deps.parchment") { mappingsVersion = it }
  }

  metadata {
    modId = "drop_confirm"
    modName = "DropConfirm"
    modVersion = "4.1.0"
    modGroup = "xyz.pupbrained.drop_confirm"
    modAuthor = "pupbrained"

    fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
      block()
    }

    replacementProperties.populate {
      put("mod_issue_tracker", "https://github.com/pupbrained/drop-confirm/issues")
      put(
        "pack_format", when (property("deps.minecraft")) {
          "1.20.1" -> 15
          "1.20.4" -> 22
          "1.20.6" -> 32
          "1.21.1" -> 34
          "1.21.3" -> 42
          "1.21.4" -> 46
          "1.21.5", "25w14craftmine" -> 55
          else -> throw IllegalArgumentException("Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
        }.toString()
      )
    }
  }

  loom {
    fabricLoaderVersion = "0.16.12"

    configureLoom { }
  }

  moddevgradle {
    enable {
      prop("deps.neoforge") { neoForgeVersion = it }
    }

    defaultRuns()

    configureNeoforge {
      runs.all {
        disableIdeRun()
      }
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

stonecutter {
  val constraint: String = name.split("-")[1]

  consts(
    "fabric" to (constraint == "fabric"),
    "neoforge" to (constraint == "neoforge"),
  )
}

dependencies {
  when (minecraft) {
    "25w14craftmine" -> modstitchModImplementation("dev.isxander:yet-another-config-lib:3.6.6+1.21.5-$loader")
    "1.20.1", "1.20.4", "1.20.6", "1.21.1", "1.21.3", "1.21.4", "1.21.5" -> modstitchModImplementation("dev.isxander:yet-another-config-lib:3.6.6+$minecraft-$loader")
    else -> throw RuntimeException("No yet-another-config-lib version defined for $minecraft")
  }

  modstitch.loom {
    modstitchModImplementation(
      "net.fabricmc.fabric-api:fabric-api:${
        when (minecraft) {
          "1.20.1" -> "0.92.5"
          "1.20.4" -> "0.97.2"
          "1.20.6" -> "0.100.8"
          "1.21.1" -> "0.115.4"
          "1.21.3" -> "0.114.0"
          "1.21.4" -> "0.119.2"
          "1.21.5" -> "0.119.9"
          "25w14craftmine" -> "0.119.8"
          else -> throw RuntimeException("No fabric api version defined for $minecraft")
        }
      }+$minecraft"
    )
    modstitchModImplementation(
      "com.terraformersmc:modmenu:${
        when (minecraft) {
          "1.20.1" -> "7.2.2"
          "1.20.4" -> "9.2.0"
          "1.20.6" -> "10.0.0"
          "1.21.1" -> "11.0.3"
          "1.21.3" -> "12.0.0"
          "1.21.4" -> "13.0.3"
          "1.21.5", "25w14craftmine" -> "14.0.0-rc.2"
          else -> throw RuntimeException("No modmenu version defined for $minecraft")
        }
      }"
    )
  }

  modstitch.moddevgradle {
    if (stonecutter.eval(minecraft, ">=1.20.5")) modstitchImplementation("thedarkcolour:kotlinforforge-neoforge:5.6.0")
    else modstitchImplementation("thedarkcolour:kotlinforforge-neoforge:4.10.0")
  }
}
