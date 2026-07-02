plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
}

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
    remap(tasks.shadowJar.get())
}

tasks.shadowJar {
    configurations = listOf(project.configurations.runtimeClasspath.get())
    archiveClassifier.set("")
}
