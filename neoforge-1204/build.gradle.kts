plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-brigadier"))
}

unimined.minecraft {
    version("1.20.4")
    mappings {
        mojmap()
    }
    neoForge {
        loader("net.neoforged:neoforge:20.4.251")
    }
    defaultRemapJar = false
}

tasks.shadowJar {
    configurations = listOf(project.configurations.runtimeClasspath.get())
    archiveClassifier.set("")
}

tasks.named("jar") {
    dependsOn(tasks.named("shadowJar"))
    enabled = false
}

tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
}


sourceSets.main.get().java.srcDir(rootProject.file("shared/neoforge/src/main/java"))
