import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

// Plugin declarations
plugins {
  id("multiloader-loader")
  id("net.neoforged.moddev")
  kotlin("jvm")
}

// Repositories
repositories {
  maven("https://maven.isxander.dev/releases/")
}

// Dependencies
dependencies {
  compileOnly(project(":common"))
  implementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}-neoforge")
}

// NeoForge configuration
neoForge {
  version = property("neoforge_version").toString()

  // Access transformers setup
  val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
  if (at.exists()) accessTransformers.from(at.absolutePath)

  parchment {
    minecraftVersion = property("parchment_minecraft").toString()
    mappingsVersion = property("parchment_version").toString()
  }

  runs {
    configureEach {
      systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id").toString())
      ideName = "NeoForge ${
        name.replaceFirstChar {
          if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
      } (${project.path})"
    }
    register("client") { client() }
    register("data") { clientData() }
    register("server") { server() }
  }

  mods.create(property("mod_id").toString()).sourceSet(sourceSets.main.get())
}

sourceSets.main.get().resources {
  srcDir("src/generated/resources")
}

dependencies {
  implementation(project(":common"))
  implementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}-neoforge")
}

tasks.compileKotlin {
  source(project(":common").sourceSets.main.get().allSource)
}

repositories {
  maven { url = uri("https://maven.isxander.dev/releases/") }
}

kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}