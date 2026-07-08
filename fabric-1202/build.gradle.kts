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
    version("1.20.2")
    mappings {
        yarn(4)
    }
    fabric {
        loader("0.15.11")
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


sourceSets.main.get().java.srcDir(rootProject.file("shared/fabric/src/main/java"))
