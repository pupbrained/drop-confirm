import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plugin declarations
plugins {
  id("fabric-loom") version "1.10-SNAPSHOT" apply false
  id("net.neoforged.moddev") version "2.0.80" apply false
  id("io.github.pacifistmc.forgix") version "1.2.9"
  kotlin("jvm")
}

// Repositories
repositories {
  mavenCentral()
  maven("https://maven.isxander.dev/releases/")
  maven("https://maven.fabricmc.net")
}

// Forgix setup
forgix {
  group = property("group") as String
  mergedJarName = property("mod_id") as String
  outputDir = "build/libs/merged"

  neoForgeContainer = NeoForgeContainer().apply {
    jarLocation = "build/libs/drop_confirm-neoforge-${property("minecraft_version")}-${property("version")}.jar"
  }
}

// Kotlin configuration
kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}
