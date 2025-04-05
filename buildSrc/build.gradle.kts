plugins {
  `kotlin-dsl`
  id("groovy-gradle-plugin")
  kotlin("jvm") version "2.1.20"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
  maven("https://maven.kikugie.dev/snapshots")
}

dependencies {
  // Make sure the version here is the same as the plugin in settings.gradle.kts
  implementation("dev.kikugie.stonecutter:dev.kikugie.stonecutter.gradle.plugin:0.6-beta.1")
}
