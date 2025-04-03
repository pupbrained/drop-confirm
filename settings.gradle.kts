pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
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
}

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "dropconfirm-multiloader"

include("common")
include("fabric")
include("neoforge")