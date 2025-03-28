import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plugin declarations
plugins {
  id("multiloader-common")
  id("net.neoforged.moddev")
  kotlin("jvm")
}

// Repositories
repositories {
  maven("https://maven.isxander.dev/releases/")
  maven("https://maven.fabricmc.net")
}

// Dependencies
dependencies {
  annotationProcessor("io.github.llamalad7:mixinextras-common:0.3.5")
  compileOnly("dev.isxander:yet-another-config-lib:${property("yacl_version")}-neoforge")
  compileOnly("io.github.llamalad7:mixinextras-common:0.3.5")
  compileOnly("org.spongepowered:mixin:0.8.5")
}

// NeoForge configuration
neoForge {
  neoFormVersion = property("neo_form_version") as String

  // Access transformers setup
  val at = file("src/main/resources/META-INF/accesstransformer.cfg")
  if (at.exists()) accessTransformers.from(at.absolutePath)

  parchment {
    minecraftVersion = property("parchment_minecraft") as String
    mappingsVersion = property("parchment_version") as String
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
artifacts {
  add("commonJava", the<SourceSetContainer>()["main"].java.sourceDirectories.singleFile)
  add("commonResources", the<SourceSetContainer>()["main"].resources.sourceDirectories.singleFile)
}

// Kotlin configuration
kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}