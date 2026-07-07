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

repositories {
    unimined.spongeMaven()
}

dependencies {
    implementation(project(":loader-forge"))
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-legacy"))
    implementation("org.spongepowered:mixin:0.8.5-SNAPSHOT")
}

unimined.minecraft {
    version("1.12.2")
    mappings {
        mcp("stable", "39-1.12")
    }
    minecraftForge {
        loader("14.23.5.2859")
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
