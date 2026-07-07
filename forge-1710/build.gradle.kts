plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.wagyourtail.jvmdowngrader") version "1.3.6"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

jvmdg.downgradeTo = JavaVersion.VERSION_1_8

dependencies {
    implementation(project(":loader-forge"))
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-legacy"))
    implementation("org.spongepowered:mixin:0.7.11-SNAPSHOT")
}

repositories {
    maven("https://mcpbot.bspk.rs/mcp")
    unimined.spongeMaven()
}

unimined.minecraft {
    version("1.7.10")
    mappings {
        mcp("snapshot", "20140925-1.7.10")
    }
    minecraftForge {
        loader("10.13.4.1614-1.7.10")
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

tasks.named("downgradeJar") {
    dependsOn(tasks.named("shadowJar"))
}

tasks.named("assemble") {
    dependsOn(tasks.named("downgradeJar"))
}
