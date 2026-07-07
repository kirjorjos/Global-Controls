plugins {
    java
    id("xyz.wagyourtail.unimined") version "1.4.1" apply false
}

val modVersion = "1.0.0"

subprojects {
    apply(plugin = "java")

    group = "net.globalcontrols"
    version = modVersion

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    repositories {
        mavenCentral()
    }
}

val artifacts = listOf(
    mapOf("module" to ":forge-1710", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.7.10"),
    mapOf("module" to ":forge-1122", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.12.2"),
    mapOf("module" to ":forge-1201", "suffix" to "", "loader" to "Forge", "mc" to "1.20.1"),
    mapOf("module" to ":fabric-1201", "suffix" to "", "loader" to "Fabric", "mc" to "1.20.1"),
    mapOf("module" to ":fabric-121", "suffix" to "", "loader" to "Fabric", "mc" to "1.21"),
    mapOf("module" to ":loader-neoforge", "suffix" to "", "loader" to "NeoForge", "mc" to "1.21"),
    mapOf("module" to ":loader-quilt", "suffix" to "", "loader" to "Quilt", "mc" to "1.21")
)

tasks.register("buildAll") {
    description = "Build all loader variants for all Minecraft versions"
    dependsOn(
        ":forge-1710:build",
        ":forge-1122:build",
        ":forge-1201:build",
        ":fabric-1201:build",
        ":fabric-121:build",
        ":loader-neoforge:build",
        ":loader-quilt:build"
    )
    group = "build"
}

tasks.register("collectArtifacts") {
    description = "Copy final distributable JARs to build/distributions/ with standardized names"
    dependsOn("buildAll")
    group = "build"

    val distDir = rootProject.layout.buildDirectory.dir("distributions")

    doLast {
        val dir = distDir.get().asFile
        dir.mkdirs()

        for (a in artifacts) {
            val projectName = a["module"]!!.removePrefix(":")
            val sourceFile = rootProject.layout.buildDirectory
                .dir("../$projectName")
                .map { it.dir("build/libs") }
                .map { it.file("$projectName-$modVersion${a["suffix"]}.jar") }
                .get().asFile

            val targetName = "GlobalControls-${a["loader"]}-${a["mc"]}-$modVersion.jar"
            val targetFile = File(dir, targetName)

            sourceFile.copyTo(targetFile, overwrite = true)
            logger.lifecycle("  ${sourceFile.name} -> ${targetFile.name}")
        }
    }
}
