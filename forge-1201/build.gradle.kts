plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    implementation(project(":loader-forge"))
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-brigadier"))
}

unimined.minecraft {
    version("1.20.1")
    mappings {
        mojmap()
    }
    minecraftForge {
        loader("47.2.0")
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
