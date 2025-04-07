pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()

    // Modstitch/YACL
    maven("https://maven.isxander.dev/releases")

    // Loom platform
    maven("https://maven.fabricmc.net/")

    // MDG platform
    maven("https://maven.neoforged.net/releases/")

    // Stonecutter
    maven("https://maven.kikugie.dev/snapshots")
  }
}

plugins {
  id("dev.kikugie.stonecutter") version "0.7-alpha.5"
}

stonecutter {
  kotlinController = true
  centralScript = "build.gradle.kts"

  create(rootProject) {
    fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
      loaders.forEach { vers("$name-$it", mcVersion) }

    mc("1.16.5", loaders = listOf("fabric"))
    mc("1.19.4", loaders = listOf("fabric"))
    mc("1.20.1", loaders = listOf("fabric"))
    mc("1.20.4", loaders = listOf("fabric", "neoforge"))
    mc("1.20.6", loaders = listOf("fabric", "neoforge"))

    mc("1.21.1", loaders = listOf("fabric", "neoforge"))
    mc("1.21.3", loaders = listOf("fabric", "neoforge"))
    mc("1.21.4", loaders = listOf("fabric", "neoforge"))
    mc("1.21.5", loaders = listOf("fabric", "neoforge"))
//    mc("25w14craftmine", loaders = listOf("fabric"))

    vcsVersion = "1.21.4-fabric"
  }
}

rootProject.name = "DropConfirm"