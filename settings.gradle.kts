import groovy.json.JsonSlurper
import java.io.File

// ---------------------------------------------------------------------------
// Plugin management
// ---------------------------------------------------------------------------
pluginManagement {
    repositories {
        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.wagyourtail.xyz/snapshots")
        mavenCentral()
        gradlePluginPortal {
            content {
                excludeGroup("org.apache.logging.log4j")
            }
        }
    }
}

plugins {
    id("xyz.wagyourtail.unimined") version "1.4.1" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "GlobalControls"

// ---------------------------------------------------------------------------
// Non-generated modules
// ---------------------------------------------------------------------------
include(":common")
project(":common").projectDir = file("src/common")

include(":platform-api")
project(":platform-api").projectDir = file("src/platform-api")

include(":platform-legacy")
project(":platform-legacy").projectDir = file("src/platform-legacy")

include(":platform-brigadier")
project(":platform-brigadier").projectDir = file("src/platform-brigadier")

// ---------------------------------------------------------------------------
// Generated per-version modules  (self-bootstrapping — no external script needed)
// Reads versions.json + templates/ and writes files during settings evaluation.
// ---------------------------------------------------------------------------

fun mcNextMinor(mc: String): String {
    val parts = mc.split(".")
    return "${parts[0]}.${parts[1].toInt() + 1}"
}

fun mcRange(mc: String): String = when (mc) {
    "1.21" -> ">=$mc"
    else   -> ">=$mc <${mcNextMinor(mc)}"
}

/** Load a template file from templates/ */
fun loadTemplate(name: String): String =
    rootDir.resolve("templates/$name").readText()

/** Write content to a file, creating parent directories. */
fun writeFile(path: File, content: String) {
    path.parentFile.mkdirs()
    path.writeText(content.trimStart() + "\n")
}

/**
 * Render a build.gradle.kts template with computed conditional blocks.
 * @param v        the version entry map
 * @param tplName  template file name under templates/
 * @param extra    additional substitutions (e.g. mcpbot_repo)
 */
fun renderBuildGradle(v: Map<String, Any>, tplName: String, extra: Map<String, String> = emptyMap()): String {
    var tpl = loadTemplate(tplName)
    val subs = v + extra

    // ---- computed conditional blocks ----
    val java = (v["java"] as? Int) ?: 17
    val javaBlock = if (java != 17)
        "\n\njava {\n    toolchain {\n        languageVersion.set(JavaLanguageVersion.of($java))\n    }\n}"
    else ""

    val downgrade = v["downgrade"] == true
    val downgradePlugin = if (downgrade)
        "\n    id(\"xyz.wagyourtail.jvmdowngrader\") version \"1.3.6\""
    else ""

    val downgradeTasks = if (downgrade)
        """
if (project.plugins.hasPlugin("xyz.wagyourtail.jvmdowngrader")) {
    jvmdg.downgradeTo = JavaVersion.VERSION_1_8
    tasks.named("downgradeJar") {
        dependsOn(tasks.named("shadowJar"))
    }
    tasks.named("assemble") {
        dependsOn(tasks.named("downgradeJar"))
    }
} else {
    tasks.named("assemble") {
        dependsOn(tasks.named("shadowJar"))
    }
}"""
    else
        """
tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
}"""

    val platformDep = if (v["platform"] == "legacy")
        "\n    implementation(project(\":platform-legacy\"))"
    else
        "\n    implementation(project(\":platform-brigadier\"))"

    // Replace computed placeholders first
    tpl = tpl.replace("{downgrade_plugin}", downgradePlugin)
    tpl = tpl.replace("{java_block}", javaBlock)
    tpl = tpl.replace("{downgrade_tasks}", downgradeTasks)
    tpl = tpl.replace("{platform_dep}", platformDep)
    tpl = tpl.replace("{mcpbot_repo}", extra["mcpbot_repo"] ?: "")

    // Replace version-entry placeholders
    for (entry in subs) {
        tpl = tpl.replace("{" + entry.key + "}", entry.value.toString())
    }

    // Squash 3+ consecutive newlines
    tpl = tpl.replace(Regex("\\n{3,}"), "\n\n")
    return tpl.trimEnd() + "\n"
}

fun renderMetadata(v: Map<String, Any>, tplName: String, extra: Map<String, String> = emptyMap()): String {
    var tpl = loadTemplate(tplName)
    val subs = v + extra
    for (entry in subs) {
        tpl = tpl.replace("{" + entry.key + "}", entry.value.toString())
    }
    return tpl
}

// ---- Generate everything ----
val versionsFile = file("versions.json")
if (!versionsFile.exists()) {
    logger.warn("versions.json not found — per-version projects will not be included. See templates/ and versions.json.")
} else {
    val versions = JsonSlurper().parse(versionsFile) as Map<String, List<Map<String, Any>>>

    for (familyEntry in versions) {
        val family = familyEntry.key
        val entries = familyEntry.value
        for (v in entries) {
            // Immutable copy with computed fields added
            val entry = v.toMutableMap()
            entry["mc_range"] = mcRange(entry["mc"] as String)
            entry["mcp_type"] = entry["mappings_val1"] ?: ""
            entry["mcp_val"] = entry["mappings_val2"] ?: ""

            val projDir = rootDir.resolve("src/$family/${entry["id"]}")
            val resDir = projDir.resolve("resources")

            // ---- BUILD.GRADLE.KTS ----
            when (family) {
                "fabric" -> {
                    entry["mapping_block"] = "yarn(${entry["yarn"]})"
                    val content = renderBuildGradle(entry, "fabric-build.gradle.kts")
                    writeFile(projDir.resolve("build.gradle.kts"), content)
                }

                "forge" -> {
                    val template = if (entry["mappings_type"] == "mojmap")
                        "forge-mojmap-build.gradle.kts"
                    else
                        "forge-mcp-build.gradle.kts"
                    val mcpbot = if (entry["mcpbot"] == true)
                        "\n\nrepositories {\n    maven(\"https://mcpbot.bspk.rs/mcp\")\n}"
                    else ""
                    val content = renderBuildGradle(entry, template, mapOf("mcpbot_repo" to mcpbot))
                    writeFile(projDir.resolve("build.gradle.kts"), content)
                }

                "neoforge" -> {
                    entry["mapping_block"] = "mojmap()" // not used in forge-mojmap template but harmless
                    var content = renderBuildGradle(entry, "forge-mojmap-build.gradle.kts")
                    if (entry["neoforged_maven"] == true) {
                        content = content.replace(
                            "dependencies {",
                            "repositories {\n    unimined.neoForgedMaven()\n}\n\ndependencies {"
                        )
                    }
                    content = content.replace("minecraftForge", "neoForge")
                    writeFile(projDir.resolve("build.gradle.kts"), content)
                }

                "quilt" -> {
                    entry["mapping_block"] = "mojmap()"
                    var content = renderBuildGradle(entry, "fabric-build.gradle.kts")
                    content = content.replace(
                        "implementation(project(\":platform-brigadier\"))",
                        "implementation(project(\":platform-brigadier\"))\n    compileOnly(\"org.quiltmc:quilt-loader:0.26.1\")"
                    )
                    if (entry["quilt_maven"] == true) {
                        content = content.replace(
                            "dependencies {",
                            "repositories {\n    unimined.quiltMaven()\n}\n\ndependencies {"
                        )
                    }
                    content = content.replace("    fabric {", "    quilt {")
                    content = content.replace("src/shared/fabric/java", "src/shared/quilt/java")
                    writeFile(projDir.resolve("build.gradle.kts"), content)
                }
            }

            // ---- METADATA ----
            when (family) {
                "fabric" -> writeFile(
                    resDir.resolve("fabric.mod.json"),
                    renderMetadata(entry, "fabric.mod.json", mapOf("loader_min" to (entry["loader"] as String)))
                )
                "quilt" -> writeFile(
                    resDir.resolve("quilt.mod.json"),
                    renderMetadata(entry, "quilt.mod.json")
                )
                "forge" -> {
                    if (entry["mcmod"] == true) {
                        writeFile(resDir.resolve("mcmod.info"), renderMetadata(entry, "mcmod.info"))
                    } else {
                        val loaderMajor = (entry["loader"] as String).split(".").first()
                        writeFile(
                            resDir.resolve("META-INF/mods.toml"),
                            renderMetadata(entry, "mods.toml", mapOf("loader_major" to loaderMajor))
                        )
                    }
                }
                "neoforge" -> writeFile(
                    resDir.resolve("META-INF/neoforge.mods.toml"),
                    renderMetadata(entry, "neoforge.mods.toml")
                )
            }

            // ---- Include ----
            include(":${entry["id"]}")
            project(":${entry["id"]}").projectDir = projDir
        }
    }
}
