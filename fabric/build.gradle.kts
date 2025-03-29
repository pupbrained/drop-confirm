import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plugin declarations
plugins {
  id("multiloader-loader")
  id("fabric-loom")
  kotlin("jvm")
}

// Repositories
repositories {
  maven("https://maven.terraformersmc.com/releases/")
  maven("https://maven.isxander.dev/releases/")
}

// Dependencies
dependencies {
  // Core dependencies
  minecraft("com.mojang:minecraft:${property("minecraft_version")}")
  mappings(loom.layered {
    officialMojangMappings()
    parchment("org.parchmentmc.data:parchment-${property("parchment_minecraft")}:${property("parchment_version")}@zip")
  })

  // Fabric dependencies
  modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
  modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
  modImplementation("net.fabricmc:fabric-language-kotlin:1.13.2+kotlin.2.1.20")

  // Other mod dependencies
  modImplementation("com.terraformersmc:modmenu:${property("modmenu_version")}")
  modImplementation("dev.isxander:yet-another-config-lib:3.6.6+1.21.5-fabric")

  // Project dependencies
  implementation(project(":common"))
}

// Loom configuration
loom {
  // Access widener setup
  val aw = project(":common").file("src/main/resources/${property("mod_id")}.accesswidener")
  if (aw.exists())
    accessWidenerPath.set(aw)

  // Mixin configuration
  mixin.defaultRefmapName.set("${property("mod_id")}.refmap.json")

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

tasks.compileKotlin {
  source(project(":common").sourceSets.main.get().allSource)
}

kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}