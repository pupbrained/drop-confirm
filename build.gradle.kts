import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Plugin declarations
plugins {
  id("fabric-loom") version "1.10-SNAPSHOT" apply false
  id("net.neoforged.moddev") version "2.0.80" apply false
  id("io.github.pacifistmc.forgix") version "1.2.9"
  id("me.modmuss50.mod-publish-plugin") version "0.8.4"
  id("co.uzzu.dotenv.gradle") version "4.0.0"
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

// Make sure jars are built before merging
tasks.named("mergeJars") {
  dependsOn(project(":neoforge").tasks.named("jar"))
  dependsOn(project(":fabric").tasks.named("remapJar"))
}

// Mod publishing
project.afterEvaluate {
  val forgixExtension = extensions.getByType(io.github.pacifistmc.forgix.plugin.ForgixMergeExtension::class.java)

  val mergedJarFileProvider = project.layout.projectDirectory.file(
    "${forgixExtension.outputDir}/${forgixExtension.mergedJarName}"
  )

  val supportedVersionsString: String = providers.gradleProperty("supported_versions").getOrElse("")

  val supportedVersionsList: List<String> = supportedVersionsString.split(',')
    .map { it.trim() }
    .filter { it.isNotEmpty() }

  publishMods {
    type = ALPHA
    displayName = "DropConfirm 4.0.0"
    changelog = """
      # DropConfirm 4.0.0
      
      * Complete Rewrite
        * Mod now uses Kotlin and multiloader
        * Jar is merged so it works for both fabric/quilt and neoforge
      * Dependency Changes
        * No more architectury
        * Fabric Language Kotlin/KotlinForForge required for fabric and neoforge, respectively
    """.trimIndent()

    github("github") {
      accessToken.set(env.GITHUB_TOKEN.orNull())
      repository.set("pupbrained/drop-confirm")
      commitish = property("minecraft_version") as String
      tagName.set("v${property("version")}-${property("minecraft_version")}")
      modLoaders.add("fabric")
      modLoaders.add("quilt")
      modLoaders.add("neoforge")
      file.set(mergedJarFileProvider)
    }

    curseforge("curseforge") {
      accessToken.set(env.CURSEFORGE_TOKEN.orNull())
      projectId.set("881314")
      minecraftVersions.addAll(supportedVersionsList)
      modLoaders.add("fabric")
      modLoaders.add("quilt")
      modLoaders.add("neoforge")
      file.set(mergedJarFileProvider)
    }

    modrinth("modrinth") {
      accessToken.set(env.MODRINTH_TOKEN.orNull())
      projectId.set("I45rjF2F")
      minecraftVersions.addAll(supportedVersionsList)
      modLoaders.add("fabric")
      modLoaders.add("quilt")
      modLoaders.add("neoforge")
      file.set(mergedJarFileProvider)
    }
  }

  tasks.named("publishGithub") {
    dependsOn(tasks.named("mergeJars"))
  }

  tasks.named("publishCurseforge") {
    dependsOn(tasks.named("mergeJars"))
  }

  tasks.named("publishModrinth") {
    dependsOn(tasks.named("mergeJars"))
  }
}

// Kotlin configuration
kotlin {
  jvmToolchain(21)
  compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}
