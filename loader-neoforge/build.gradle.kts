plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-brigadier"))
}

repositories {
    unimined.neoForgedMaven()
}

unimined.minecraft {
    version("1.21")
    mappings {
        mojmap()
    }
    neoForge {
        loader("net.neoforged:neoforge:21.0.0-beta")
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
