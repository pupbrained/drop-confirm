import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plugin declarations
plugins {
  id("multiloader-common")
  id("net.neoforged.moddev") version "2.0.80"
  kotlin("jvm")
}

// Repositories
repositories {
  maven("https://maven.isxander.dev/releases/")
  maven("https://maven.fabricmc.net")
  maven("https://maven.kikugie.dev/snapshots")
}

// Dependencies
dependencies {
  annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")
  compileOnly("dev.isxander:yet-another-config-lib:${versionProp("yacl")}-neoforge")
  compileOnly("io.github.llamalad7:mixinextras-common:0.3.5")
  compileOnly("org.spongepowered:mixin:0.8.5")
}

// NeoForge configuration
neoForge {
  neoFormVersion = versionProp("neo_form")

  parchment {
    minecraftVersion = versionProp("parchment_minecraft")
    mappingsVersion = versionProp("parchment")
  }
}

// Custom configurations
configurations {
  create("commonJava") {
    isCanBeResolved = false
    isCanBeConsumed = true
  }

  create("commonResources") {
    isCanBeResolved = false
    isCanBeConsumed = true
  }
}

// Artifacts
val mainSourceSet: SourceSet = the<SourceSetContainer>()["main"]
artifacts {
  add("commonJava", mainSourceSet.java.sourceDirectories.files.first())
  add("commonResources", mainSourceSet.resources.sourceDirectories.files.first())
}

// Kotlin configuration
kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}