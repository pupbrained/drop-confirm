pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.kikugie.dev/snapshots")
    exclusiveContent {
      forRepository {
        maven {
          name = "Fabric"
          url = uri("https://maven.fabricmc.net")
        }
      }
      filter {
        includeGroup("net.fabricmc")
        includeGroup("fabric-loom")
      }
    }
  }

  plugins {
    kotlin("jvm") version "2.1.20"
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
  id("dev.kikugie.stonecutter") version "0.6-beta.1"
}

stonecutter {
  centralScript = "build.gradle.kts"
  kotlinController = true

  create(rootProject) {
    versions("1.21.1", "1.21.4", "1.21.5")
    vcsVersion.set("1.21.4")
    branch("common")
    branch("fabric")
    branch("neoforge")
  }
}

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "dropconfirm-multiloader"

include("common")
include("fabric")
include("neoforge")