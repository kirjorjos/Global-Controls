import groovy.json.JsonSlurper
import java.io.File

plugins {
    java
    id("xyz.wagyourtail.unimined") version "1.4.1" apply false
}

version = "1.0.1"

subprojects {
    apply(plugin = "java")

    group = rootProject.group

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
    }
}

subprojects {
    // After flattening, source dirs are at java/ and resources/ (not src/main/java/)
    sourceSets.main.get().java.srcDir("java")
    sourceSets.main.get().resources.srcDir("resources")
}

// Load version data from versions.json
data class VersionEntry(
    val id: String,
    val mc: String,
    val downgrade: Boolean,
    val modVersion: String
)

fun loadVersions(): List<VersionEntry> {
    val file = rootProject.file("versions.json")
    if (!file.exists()) return emptyList()
    val versions = JsonSlurper().parse(file) as Map<String, List<Map<String, Any>>>
    return versions.flatMap { (family, entries) ->
        entries.map { entry ->
            val mc = entry["mc"] as String
            // Compute display suffix
            val suffix = if (entry["downgrade"] == true) "-downgraded" else ""
            VersionEntry(
                id = entry["id"] as String,
                mc = mc,
                downgrade = entry["downgrade"] == true,
                modVersion = entry["modVersion"] as? String ?: "1.0.0"
            )
        }
    }
}

fun loaderDisplayName(id: String): String = when {
    id.startsWith("fabric-") -> "Fabric"
    id.startsWith("forge-") -> "Forge"
    id.startsWith("neoforge-") -> "NeoForge"
    id.startsWith("quilt-") -> "Quilt"
    else -> id
}

val versionEntries = loadVersions()

// --- Tests (delegate to common module) ---
tasks.named("test") {
    dependsOn(":common:test")
    enabled = false
}

tasks.register("buildAll") {
    description = "Build all loader variants for all Minecraft versions"
    for (entry in versionEntries) {
        dependsOn(":${entry.id}:build")
    }
    group = "build"
}

// --- Collect artifacts ---
tasks.register("collectArtifacts") {
    description = "Copy final distributable JARs to build/distributions/ with standardized names"
    dependsOn("buildAll")
    group = "build"

    val distDir = rootProject.layout.buildDirectory.dir("distributions")

    doLast {
        val dir = distDir.get().asFile
        dir.mkdirs()

        for (entry in versionEntries) {
            val projDir = project(":${entry.id}").projectDir
            val suffix = if (entry.downgrade) "-downgraded" else ""
            val sourceFile = projDir.resolve("build/libs/${entry.id}$suffix.jar")
            val loaderName = loaderDisplayName(entry.id)
            val targetName = "GlobalControls-$loaderName-${entry.mc}-$version.jar"
            val targetFile = File(dir, targetName)

            if (sourceFile.exists()) {
                sourceFile.copyTo(targetFile, overwrite = true)
                logger.lifecycle("  ${sourceFile.name} -> ${targetFile.name}")
            } else {
                logger.warn("  Skipping ${sourceFile.name} (not found)")
            }
        }
    }
}
