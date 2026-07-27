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

val artifacts = listOf(
    // Era 0 — Java 8 via downgrader
    mapOf("module" to ":forge-1_7_10", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.7.10"),
    mapOf("module" to ":forge-1_12_2", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.12.2"),
    mapOf("module" to ":forge-1_14_4", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.14.4"),
    mapOf("module" to ":forge-1_15_2", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.15.2"),
    mapOf("module" to ":forge-1_16_5", "suffix" to "-downgraded", "loader" to "Forge", "mc" to "1.16.5"),
    mapOf("module" to ":fabric-1_14_4", "suffix" to "-downgraded", "loader" to "Fabric", "mc" to "1.14.4"),
    mapOf("module" to ":fabric-1_15_2", "suffix" to "-downgraded", "loader" to "Fabric", "mc" to "1.15.2"),
    mapOf("module" to ":fabric-1_16_5", "suffix" to "-downgraded", "loader" to "Fabric", "mc" to "1.16.5"),
    // Era 1 — Java 17
    mapOf("module" to ":forge-1_17_1", "suffix" to "", "loader" to "Forge", "mc" to "1.17.1"),
    mapOf("module" to ":forge-1_18_2", "suffix" to "", "loader" to "Forge", "mc" to "1.18.2"),
    mapOf("module" to ":forge-1_19_4", "suffix" to "", "loader" to "Forge", "mc" to "1.19.4"),
    mapOf("module" to ":forge-1_20_1", "suffix" to "", "loader" to "Forge", "mc" to "1.20.1"),
    mapOf("module" to ":forge-1_20_2", "suffix" to "", "loader" to "Forge", "mc" to "1.20.2"),
    mapOf("module" to ":forge-1_20_4", "suffix" to "", "loader" to "Forge", "mc" to "1.20.4"),
    mapOf("module" to ":fabric-1_17_1", "suffix" to "", "loader" to "Fabric", "mc" to "1.17.1"),
    mapOf("module" to ":fabric-1_18_2", "suffix" to "", "loader" to "Fabric", "mc" to "1.18.2"),
    mapOf("module" to ":fabric-1_19_4", "suffix" to "", "loader" to "Fabric", "mc" to "1.19.4"),
    mapOf("module" to ":fabric-1_20_1", "suffix" to "", "loader" to "Fabric", "mc" to "1.20.1"),
    mapOf("module" to ":fabric-1_20_2", "suffix" to "", "loader" to "Fabric", "mc" to "1.20.2"),
    mapOf("module" to ":fabric-1_20_4", "suffix" to "", "loader" to "Fabric", "mc" to "1.20.4"),
    mapOf("module" to ":quilt-1_18_2", "suffix" to "", "loader" to "Quilt", "mc" to "1.18.2"),
    mapOf("module" to ":quilt-1_19_4", "suffix" to "", "loader" to "Quilt", "mc" to "1.19.4"),
    // Era 2 — Java 21
    mapOf("module" to ":fabric-1_21", "suffix" to "", "loader" to "Fabric", "mc" to "1.21"),
    mapOf("module" to ":neoforge-1_20_2", "suffix" to "", "loader" to "NeoForge", "mc" to "1.20.2"),
    mapOf("module" to ":neoforge-1_20_4", "suffix" to "", "loader" to "NeoForge", "mc" to "1.20.4"),
    mapOf("module" to ":neoforge-1_21", "suffix" to "", "loader" to "NeoForge", "mc" to "1.21"),
    mapOf("module" to ":quilt-1_21", "suffix" to "", "loader" to "Quilt", "mc" to "1.21")
)

tasks.register("buildAll") {
    description = "Build all loader variants for all Minecraft versions"
    dependsOn(
        ":forge-1_7_10:build",
        ":forge-1_12_2:build",
        ":forge-1_14_4:build",
        ":forge-1_15_2:build",
        ":forge-1_16_5:build",
        ":forge-1_17_1:build",
        ":forge-1_18_2:build",
        ":forge-1_19_4:build",
        ":forge-1_20_1:build",
        ":forge-1_20_2:build",
        ":forge-1_20_4:build",
        ":fabric-1_14_4:build",
        ":fabric-1_15_2:build",
        ":fabric-1_16_5:build",
        ":fabric-1_17_1:build",
        ":fabric-1_18_2:build",
        ":fabric-1_19_4:build",
        ":fabric-1_20_1:build",
        ":fabric-1_20_2:build",
        ":fabric-1_20_4:build",
        ":fabric-1_21:build",
        ":quilt-1_18_2:build",
        ":quilt-1_19_4:build",
        ":quilt-1_21:build",
        ":neoforge-1_20_2:build",
        ":neoforge-1_20_4:build",
        ":neoforge-1_21:build"
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
            val projDir = project(":$projectName").projectDir
            val sourceFile = projDir.resolve("build/libs/$projectName${a["suffix"]}.jar")

            val targetName = "GlobalControls-${a["loader"]}-${a["mc"]}-$version.jar"
            val targetFile = File(dir, targetName)

            sourceFile.copyTo(targetFile, overwrite = true)
            logger.lifecycle("  ${sourceFile.name} -> ${targetFile.name}")
        }
    }
}
