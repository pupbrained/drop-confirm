import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

// Plugin declarations
plugins {
  id("multiloader-loader")
  id("net.neoforged.moddev") version "2.0.80"
  kotlin("jvm")
}

// Repositories
repositories {
  maven("https://maven.isxander.dev/releases/")
}

// Dependencies
dependencies {
  val commonProject = stonecutter.node.sibling("common")
  implementation(project(path = commonProject!!.project.path, configuration = "commonJava"))
  implementation("dev.isxander:yet-another-config-lib:${versionProp("yacl")}-neoforge")
}

// NeoForge configuration
neoForge {
  version = versionProp("neoforge")

  parchment {
    minecraftVersion = versionProp("parchment_minecraft")
    mappingsVersion = versionProp("parchment")
  }

  runs {
    configureEach {
      systemProperty("neoforge.enabledGameTestNamespaces", prop("mod.id"))
      ideName = "NeoForge ${
        name.replaceFirstChar {
          if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
      } (${project.path})"
    }
    register("client") { client() }
    register("data") {
      if (stonecutter.eval(stonecutter.current.version, ">1.21.1"))
        clientData()
      else
        data()
    }
    register("server") { server() }
  }

  mods.create(prop("mod.id")!!).sourceSet(sourceSets.main.get())
}

sourceSets.main.get().resources {
  srcDir("src/generated/resources")
}

kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}