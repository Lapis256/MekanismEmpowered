pluginManagement {
    repositories {
        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
        }
//        maven {
//            name = "ModPublisher"
//            url = uri("https://maven.firstdarkdev.xyz/releases")
//        }
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = file("buildSrc/src/main/kotlin/Constants.kt").readLines()
    .map(String::trim)
    .find { it.startsWith("const val NAME") }
    ?.substringAfter('"')
    ?.substringBefore('"')
    ?.replace(" ", "")
    ?.replace(":", "")
    ?: throw IllegalStateException("Failed to find mod NAME")
