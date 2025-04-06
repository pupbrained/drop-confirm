import io.github.pacifistmc.forgix.plugin.ForgixMergeExtension

plugins {
  id("dev.kikugie.stonecutter")
  id("io.github.pacifistmc.forgix") version "1.2.9"
}

stonecutter active "1.21.5-fabric"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
  group = "project"
  ofTask("build")
}

// Register a task to merge JARs after building all variants
tasks.register("mergeAllJars") {
  group = "forgix"
  description = "Merges all built JARs for a specific Minecraft version"

  // This will run after all versions are built
  dependsOn("chiseledBuild")
  finalizedBy("mergeJars")
}

listOf("1.20.4", "1.21.1", "1.21.4", "1.21.5").forEach { mcVersion ->
  tasks.register("merge${mcVersion.replace(".", "")}") {
    group = "forgix"
    description = "Builds and merges $mcVersion versions"

    // This will ensure we build the right versions first
    dependsOn(
      ":${mcVersion}-fabric:build",
      ":${mcVersion}-neoforge:build"
    )

    doLast {
      println("Configuring Forgix for Minecraft $mcVersion")

      // Point to the specific version JARs
      project.rootProject.configure<ForgixMergeExtension> {
        group = "xyz.pupbrained.drop_confirm"
        mergedJarName = "DropConfirm-${mcVersion}-merged.jar"
        removeDuplicate("xyz.pupbrained.drop_confirm.config")
        removeDuplicate("xyz.pupbrained.drop_confirm.DropConfirm")

        fabricContainer = FabricContainer().apply {
          projectName = "$mcVersion-fabric"
          jarLocation = "build/libs/drop_confirm-4.1.0.jar"
        }

        neoForgeContainer = NeoForgeContainer().apply {
          projectName = "$mcVersion-neoforge"
          jarLocation = "build/libs/drop_confirm-4.1.0.jar"
        }
      }

      // Now directly execute the mergeJars task
      println("Executing mergeJars task...")
      tasks.getByName("mergeJars").actions.forEach { action ->
        action.execute(tasks.getByName("mergeJars"))
      }
      println("mergeJars task completed")
    }
  }
}

tasks.register("findMergedJars") {
  group = "forgix"
  description = "Find merged JAR files"

  doLast {
    // Check standard locations
    val locations = listOf(
      File(rootProject.projectDir, "Merged"),
      File(rootProject.projectDir, "build/libs/merged"),
      File(rootProject.projectDir, "build/libs")
    )

    locations.forEach { dir ->
      println("Checking directory: ${dir.absolutePath} (exists: ${dir.exists()})")
      if (dir.exists()) {
        dir.listFiles()?.forEach { file ->
          println("  Found file: ${file.name}")
        }
      }
    }
  }
}

allprojects {
  repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.fabricmc.net/")

    maven("https://maven.isxander.dev/releases/")
    maven("https://maven.nucleoid.xyz/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
  }
}