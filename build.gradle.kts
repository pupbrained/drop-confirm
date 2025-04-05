@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
  mergedJarName = "${prop("mod.id")}-${prop("mod.version")}-${prop("vers.minecraft")}.jar"
  outputDir = "build/libs/merged"

  neoForgeContainer = NeoForgeContainer().apply {
    jarLocation = "build/libs/${prop("mod.id")}-neoforge-${prop("vers.minecraft")}-${prop("mod.version")}.jar"
  }
}

tasks.named("mergeJars") {
  dependsOn(project(":neoforge").tasks.named("jar"))
  dependsOn(project(":fabric").tasks.named("remapJar"))
}

//project.afterEvaluate {
//  val envFilePath = rootDir.resolve(".env")
//
//  val envVars = DotEnvBuilder.dotEnv {
//    addSystemEnv()
//
//    if (envFilePath.exists() && envFilePath.isFile)
//      addFile(envFilePath.absolutePath)
//  }
//
//  val forgixExtension = extensions.getByType(io.github.pacifistmc.forgix.plugin.ForgixMergeExtension::class.java)
//
//  val mergedJarFileProvider = project.layout.projectDirectory.file(
//    "${forgixExtension.outputDir}/${forgixExtension.mergedJarName}"
//  )
//
//  val releaseDisplayName = "${prop("mod.name")} ${prop("mod.version")} (${prop("vers.minecraft")})"
//
//  publishMods {
//    type = STABLE
//    displayName = releaseDisplayName
//    changelog = """
//      # ${prop("mod.name")} ${prop("mod.version")}
//
//      * Complete Rewrite
//        * Mod now uses Kotlin and multiloader
//        * Jar is merged so it works for both Fabric/Quilt and NeoForge
//      * Dependency Changes
//        * No more Architectury
//        * Fabric Language Kotlin/KotlinForForge required for Fabric and NeoForge, respectively
//    """.trimIndent()
//
//    github("github") {
//      accessToken.set(envVars["GITHUB_TOKEN"])
//      repository.set("pupbrained/drop-confirm")
//      commitish.set(prop("vers.minecraft"))
//      tagName.set("v${prop("mod.version")}-${prop("vers.minecraft")}")
//      modLoaders.add("fabric")
//      modLoaders.add("quilt")
//      modLoaders.add("neoforge")
//      file.set(mergedJarFileProvider)
//    }
//
//    curseforge("curseforge") {
//      accessToken.set(envVars["CURSEFORGE_TOKEN"])
//      projectId.set("881314")
//      minecraftVersions.add(prop("vers.minecraft"))
//      modLoaders.add("fabric")
//      modLoaders.add("quilt")
//      modLoaders.add("neoforge")
//      file.set(mergedJarFileProvider)
//
//      requires("yacl")
//      optional(
//        "modmenu",
//        "fabric-language-kotlin",
//        "kotlin-for-forge"
//      )
//    }
//
//    modrinth("modrinth") {
//      accessToken.set(envVars["MODRINTH_TOKEN"])
//      projectId.set("I45rjF2F")
//      minecraftVersions.add(prop("vers.minecraft"))
//      modLoaders.add("fabric")
//      modLoaders.add("quilt")
//      modLoaders.add("neoforge")
//      file.set(mergedJarFileProvider)
//
//      requires("yacl")
//      optional(
//        "modmenu",
//        "fabric-language-kotlin",
//        "kotlin-for-forge"
//      )
//    }
//  }
//
//  listOf("publishGithub", "publishCurseforge", "publishModrinth").forEach {
//    tasks.named(it) {
//      dependsOn(tasks.named("mergeJars"))
//    }
//  }
//}

kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}
