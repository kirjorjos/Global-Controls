plugins {
    id("xyz.wagyourtail.jvmdowngrader")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-legacy"))
    implementation(project(":platform-brigadier"))
}

data class ForgeTarget(val mcVer: String, val platform: String)

val targets = listOf(
    ForgeTarget("1.7.10", "legacy"),
    ForgeTarget("1.12.2", "legacy"),
    ForgeTarget("1.20.1", "brigadier")
)

targets.forEach { target ->
    val safe = target.mcVer.replace('.', '-')
    val platformProj = if (target.platform == "legacy") ":platform-legacy" else ":platform-brigadier"
    val jarName = "globalcontrols-forge-${target.mcVer}"

    val jarTask = tasks.register("jarForge-$safe", Jar::class) {
        group = "build"
        archiveFileName.set("$jarName-fat.jar")
        dependsOn(":common:jar", ":platform-api:jar", platformProj + ":jar")
        from(tasks.named("classes"))
        from(project(":common").tasks.jar.map { zipTree(it.archiveFile) })
        from(project(":platform-api").tasks.jar.map { zipTree(it.archiveFile) })
        from(project(platformProj).tasks.jar.map { zipTree(it.archiveFile) })
        from(configurations.runtimeClasspath.map { cp ->
            cp.filter { it.name.contains("gson") }.map { zipTree(it) }
        })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    val downgradeTask = tasks.register("downgradeForge-$safe", xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar::class) {
        group = "build"
        inputFile = jarTask.flatMap { it.archiveFile }
        downgradeTo = JavaVersion.VERSION_1_8
        archiveFileName.set("$jarName.jar")
    }

    tasks.register("buildForge-$safe") {
        group = "build"
        dependsOn(downgradeTask)
    }
}

tasks.register("buildForgeAll") {
    group = "build"
    dependsOn(tasks.matching { it.name.startsWith("buildForge-") })
}
