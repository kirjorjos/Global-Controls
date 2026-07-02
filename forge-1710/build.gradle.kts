plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
}

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
    remap(tasks.shadowJar.get())
}

tasks.shadowJar {
    configurations = listOf(project.configurations.runtimeClasspath.get())
    archiveClassifier.set("")
}
