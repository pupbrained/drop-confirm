@file:Suppress("PropertyName")

import io.github.klahap.dotenv.DotEnvBuilder
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val group: String by project
val mod_id: String by project
val mod_name: String by project
val minecraft_version: String by project
val mod_version: String by project
val supported_versions: String by project

plugins {
  id("fabric-loom") version "1.10-SNAPSHOT" apply false
  id("net.neoforged.moddev") version "2.0.80" apply false
  id("io.github.pacifistmc.forgix") version "1.2.9"
  id("me.modmuss50.mod-publish-plugin") version "0.8.4"
  id("io.github.klahap.dotenv") version "1.1.3"
  kotlin("jvm")
}

repositories {
  mavenCentral()
  maven("https://maven.isxander.dev/releases/")
  maven("https://maven.fabricmc.net")
}

forgix {
  group = property("group") as String
  mergedJarName = "$mod_id-$mod_version-$minecraft_version.jar"
  outputDir = "build/libs/merged"

  neoForgeContainer = NeoForgeContainer().apply {
    jarLocation = "build/libs/$mod_id-neoforge-$minecraft_version-$mod_version.jar"
  }
}

tasks.named("mergeJars") {
  dependsOn(project(":neoforge").tasks.named("jar"))
  dependsOn(project(":fabric").tasks.named("remapJar"))
}

project.afterEvaluate {
  val envFilePath = rootDir.resolve(".env")

  val envVars = DotEnvBuilder.dotEnv {
    addSystemEnv()

    if (envFilePath.exists() && envFilePath.isFile)
      addFile(envFilePath.absolutePath)
  }

  val forgixExtension = extensions.getByType(io.github.pacifistmc.forgix.plugin.ForgixMergeExtension::class.java)

  val mergedJarFileProvider = project.layout.projectDirectory.file(
    "${forgixExtension.outputDir}/${forgixExtension.mergedJarName}"
  )

  val supportedVersionsList = supported_versions.split(',')
    .map { it.trim() }
    .filter { it.isNotEmpty() }

  val versionRangeString: String = when {
    supportedVersionsList.isEmpty() -> ""
    supportedVersionsList.size == 1 -> supportedVersionsList.first()
    else -> "${supportedVersionsList.first()}-${supportedVersionsList.last()}"
  }

  val displayVersionSuffix = if (versionRangeString.isNotEmpty()) " ($versionRangeString)" else ""
  val releaseDisplayName = "$mod_name $mod_version$displayVersionSuffix"

  publishMods {
    type = STABLE
    displayName = releaseDisplayName
    changelog = """
      # $mod_name $mod_version
      
      * Complete Rewrite
        * Mod now uses Kotlin and multiloader
        * Jar is merged so it works for both Fabric/Quilt and NeoForge
      * Dependency Changes
        * No more Architectury
        * Fabric Language Kotlin/KotlinForForge required for Fabric and NeoForge, respectively
    """.trimIndent()

    github("github") {
      accessToken.set(envVars["GITHUB_TOKEN"])
      repository.set("pupbrained/drop-confirm")
      commitish.set(minecraft_version)
      tagName.set("v$mod_version")
      modLoaders.add("fabric")
      modLoaders.add("quilt")
      modLoaders.add("neoforge")
      file.set(mergedJarFileProvider)
    }

    curseforge("curseforge") {
      accessToken.set(envVars["CURSEFORGE_TOKEN"])
      projectId.set("881314")
      minecraftVersions.addAll(supportedVersionsList)
      modLoaders.add("fabric")
      modLoaders.add("quilt")
      modLoaders.add("neoforge")
      file.set(mergedJarFileProvider)

      requires("yacl")
      optional("modmenu")
      optional("fabric-language-kotlin")
      optional("kotlin-for-forge")
    }

    modrinth("modrinth") {
      accessToken.set(envVars["MODRINTH_TOKEN"])
      projectId.set("I45rjF2F")
      minecraftVersions.addAll(supportedVersionsList)
      modLoaders.add("fabric")
      modLoaders.add("quilt")
      modLoaders.add("neoforge")
      file.set(mergedJarFileProvider)

      requires("yacl")
      optional("modmenu")
      optional("fabric-language-kotlin")
      optional("kotlin-for-forge")
    }
  }

  listOf("publishGithub", "publishCurseforge", "publishModrinth").forEach {
    tasks.named(it) {
      dependsOn(tasks.named("mergeJars"))
    }
  }
}

kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}
