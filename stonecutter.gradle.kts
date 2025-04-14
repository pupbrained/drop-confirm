plugins { id("dev.kikugie.stonecutter") }

stonecutter active "1.21.4-fabric"

tasks.register("Build active project") {
  group = "stonecutter"
  dependsOn(":${stonecutter.current.project}:build")
}

tasks.register("Run active project") {
  group = "stonecutter"
  dependsOn(":${stonecutter.current.project}:runClient")
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
    maven("https://maven.firstdark.dev/snapshots")
    maven("https://api.modrinth.com/maven")
  }
}
