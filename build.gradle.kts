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

val artifacts = listOf(
    // Era 0 — Java 8 via downgrader
    mapOf("module" to ":forge-1710", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.7.10"),
    mapOf("module" to ":forge-1122", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.12.2"),
    mapOf("module" to ":forge-1144", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.14.4"),
    mapOf("module" to ":forge-1152", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.15.2"),
    mapOf("module" to ":forge-1165", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.16.5"),
    mapOf("module" to ":fabric-1144", "suffix" to "-downgraded", "loader" to "Fabric", "mc" to "1.14.4"),
    mapOf("module" to ":fabric-1152", "suffix" to "-downgraded", "loader" to "Fabric", "mc" to "1.15.2"),
    mapOf("module" to ":fabric-1165", "suffix" to "-downgraded", "loader" to "Fabric", "mc" to "1.16.5"),
    // Era 1 — Java 17
    mapOf("module" to ":forge-1171", "suffix" to "", "loader" to "Forge", "mc" to "1.17.1"),
    mapOf("module" to ":forge-1182", "suffix" to "", "loader" to "Forge", "mc" to "1.18.2"),
    mapOf("module" to ":forge-1194", "suffix" to "", "loader" to "Forge", "mc" to "1.19.4"),
    mapOf("module" to ":forge-1201", "suffix" to "", "loader" to "Forge", "mc" to "1.20.1"),
    mapOf("module" to ":forge-1202", "suffix" to "", "loader" to "Forge", "mc" to "1.20.2"),
    mapOf("module" to ":forge-1204", "suffix" to "", "loader" to "Forge", "mc" to "1.20.4"),
    mapOf("module" to ":fabric-1171", "suffix" to "", "loader" to "Fabric", "mc" to "1.17.1"),
    mapOf("module" to ":fabric-1182", "suffix" to "", "loader" to "Fabric", "mc" to "1.18.2"),
    mapOf("module" to ":fabric-1194", "suffix" to "", "loader" to "Fabric", "mc" to "1.19.4"),
    mapOf("module" to ":fabric-1201", "suffix" to "", "loader" to "Fabric", "mc" to "1.20.1"),
    mapOf("module" to ":fabric-1202", "suffix" to "", "loader" to "Fabric", "mc" to "1.20.2"),
    mapOf("module" to ":fabric-1204", "suffix" to "", "loader" to "Fabric", "mc" to "1.20.4"),
    mapOf("module" to ":quilt-1182", "suffix" to "", "loader" to "Quilt", "mc" to "1.18.2"),
    mapOf("module" to ":quilt-1194", "suffix" to "", "loader" to "Quilt", "mc" to "1.19.4"),
    // Era 2 — Java 21
    mapOf("module" to ":fabric-121", "suffix" to "", "loader" to "Fabric", "mc" to "1.21"),
    mapOf("module" to ":neoforge-1202", "suffix" to "", "loader" to "NeoForge", "mc" to "1.20.2"),
    mapOf("module" to ":neoforge-1204", "suffix" to "", "loader" to "NeoForge", "mc" to "1.20.4"),
    mapOf("module" to ":neoforge-121", "suffix" to "", "loader" to "NeoForge", "mc" to "1.21"),
    mapOf("module" to ":quilt-121", "suffix" to "", "loader" to "Quilt", "mc" to "1.21")
)

tasks.register("buildAll") {
    description = "Build all loader variants for all Minecraft versions"
    dependsOn(
        ":forge-1710:build",
        ":forge-1122:build",
        ":forge-1144:build",
        ":forge-1152:build",
        ":forge-1165:build",
        ":forge-1171:build",
        ":forge-1182:build",
        ":forge-1194:build",
        ":forge-1201:build",
        ":forge-1202:build",
        ":forge-1204:build",
        ":fabric-1144:build",
        ":fabric-1152:build",
        ":fabric-1165:build",
        ":fabric-1171:build",
        ":fabric-1182:build",
        ":fabric-1194:build",
        ":fabric-1201:build",
        ":fabric-1202:build",
        ":fabric-1204:build",
        ":fabric-121:build",
        ":quilt-1182:build",
        ":quilt-1194:build",
        ":quilt-121:build",
        ":neoforge-1202:build",
        ":neoforge-1204:build",
        ":neoforge-121:build"
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
                .map { it.file("$projectName${a["suffix"]}.jar") }
                .get().asFile

            val targetName = "GlobalControls-${a["loader"]}-${a["mc"]}-$version.jar"
            val targetFile = File(dir, targetName)

            sourceFile.copyTo(targetFile, overwrite = true)
            logger.lifecycle("  ${sourceFile.name} -> ${targetFile.name}")
        }
    }
}
