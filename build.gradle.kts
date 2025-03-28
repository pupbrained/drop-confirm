import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plugin declarations
plugins {
  id("fabric-loom") version "1.10-SNAPSHOT" apply false
  id("net.neoforged.moddev") version "2.0.80" apply false
  kotlin("jvm")
}

// Repositories
repositories {
  mavenCentral()
  maven("https://maven.isxander.dev/releases/")
  maven("https://maven.fabricmc.net")
}

// Kotlin configuration
kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}
