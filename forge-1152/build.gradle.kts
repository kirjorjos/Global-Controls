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

repositories {
    maven("https://mcpbot.bspk.rs/mcp")
    unimined.spongeMaven()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-brigadier"))
    implementation("org.spongepowered:mixin:0.8.5-SNAPSHOT")
}

unimined.minecraft {
    version("1.15.2")
    mappings {
        mcp("stable", "60-1.15")
    }
    minecraftForge {
        loader("31.2.60")
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
}


sourceSets.main.get().java.srcDir(rootProject.file("shared/forge-old/src/main/java"))
