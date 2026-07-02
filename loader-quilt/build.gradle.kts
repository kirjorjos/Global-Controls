plugins {
    id("xyz.wagyourtail.jvmdowngrader")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-brigadier"))
}

val mcVersions = listOf("1.21")

mcVersions.forEach { mcVer ->
    val safe = mcVer.replace('.', '-')
    val jarName = "globalcontrols-quilt-$mcVer"

    val jarTask = tasks.register("jarQuilt-$safe", Jar::class) {
        group = "build"
        archiveFileName.set("$jarName-fat.jar")
        dependsOn(":common:jar", ":platform-api:jar", ":platform-brigadier:jar")
        from(tasks.named("classes"))
        from(project(":common").tasks.jar.map { zipTree(it.archiveFile) })
        from(project(":platform-api").tasks.jar.map { zipTree(it.archiveFile) })
        from(project(":platform-brigadier").tasks.jar.map { zipTree(it.archiveFile) })
        from(configurations.runtimeClasspath.map { cp ->
            cp.filter { it.name.contains("gson") }.map { zipTree(it) }
        })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    val downgradeTask = tasks.register("downgradeQuilt-$safe", xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar::class) {
        group = "build"
        inputFile = jarTask.flatMap { it.archiveFile }
        downgradeTo = JavaVersion.VERSION_1_8
        archiveFileName.set("$jarName.jar")
    }

    tasks.register("buildQuilt-$safe") {
        group = "build"
        dependsOn(downgradeTask)
    }
}

tasks.register("buildQuiltAll") {
    group = "build"
    dependsOn(tasks.matching { it.name.startsWith("buildQuilt-") })
}
