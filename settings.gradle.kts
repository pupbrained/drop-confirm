pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.kikugie.dev/snapshots")
  }
}

plugins { id("dev.kikugie.stonecutter") version "0.7-alpha.6" }

stonecutter {
  kotlinController = true
  centralScript = "build.gradle.kts"

  create(rootProject) {
    fun mc(mcVersion: String, vararg loaders: String) = loaders.forEach { vers("$mcVersion-$it", mcVersion) }

    fun mcSnapshot(name: String, mcVersion: String) {
      val (year, week, suffix) = (Regex("(\\d+)w(\\d{2})(.*)").find(name)
        ?: throw IllegalArgumentException("Invalid snapshot name")).destructured

      vers("$name-fabric", "$mcVersion-alpha.$year.$week.$suffix")
    }

    mc("1.14.4", "fabric")
    mc("1.15.2", "fabric")
    mc("1.16.5", "fabric")
    mc("1.17.1", "fabric")

    mc("1.18.2", "fabric", "forge")
    mc("1.19.4", "fabric", "forge")
    mc("1.20.1", "fabric", "forge")

    mc("1.20.4", "fabric")

    mc("1.20.6", "fabric", "neoforge")
    mc("1.21.1", "fabric", "neoforge")
    mc("1.21.3", "fabric", "neoforge")
    mc("1.21.4", "fabric", "neoforge")
    mc("1.21.5", "fabric", "neoforge")

    mcSnapshot("25w14craftmine", "1.21.6")
    mcSnapshot("25w15a", "1.21.6")
    mcSnapshot("25w16a", "1.21.6")

    vcsVersion = "1.21.4-fabric"
  }
}

rootProject.name = "DropConfirm"
