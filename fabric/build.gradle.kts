import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plugin declarations
plugins {
  id("multiloader-loader")
  id("fabric-loom") version "1.10-SNAPSHOT"
  kotlin("jvm")
}

// Repositories
repositories {
  maven("https://maven.terraformersmc.com/releases/")
  maven("https://maven.isxander.dev/releases/")
}

// Dependencies
dependencies {
  val commonProject = stonecutter.node.sibling("common")

  // Core dependencies
  minecraft("com.mojang:minecraft:${stonecutter.current.version}")
  mappings(loom.layered {
    officialMojangMappings()
    parchment("org.parchmentmc.data:parchment-${versionProp("parchment_minecraft")}:${versionProp("parchment")}@zip")
  })

  // Fabric dependencies
  modImplementation("net.fabricmc:fabric-loader:${versionProp("fabric_loader")}")
  modImplementation("net.fabricmc.fabric-api:fabric-api:${versionProp("fabric")}")
  modImplementation("net.fabricmc:fabric-language-kotlin:1.13.2+kotlin.2.1.20")

  // Other mod dependencies
  modImplementation("com.terraformersmc:modmenu:${versionProp("modmenu")}")
  modImplementation("dev.isxander:yet-another-config-lib:${versionProp("yacl")}-fabric")

  // Project dependencies
  implementation(project(path = commonProject!!.project.path, configuration = "commonJava"))
}

// Loom configuration
loom {
  // Mixin configuration
  mixin.defaultRefmapName.set("${prop("mod.id")}.refmap.json")

  // Run configurations
  runs {
    create("fabricClient") {
      client()
      configName = "Fabric Client"
      runDir = "runs/client"
    }
    create("fabricServer") {
      server()
      configName = "Fabric Server"
      runDir = "runs/server"
    }
  }
}

kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}