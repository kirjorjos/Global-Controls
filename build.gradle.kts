plugins {
    java
    id("xyz.wagyourtail.jvmdowngrader") version "1.3.6" apply false
}

subprojects {
    apply(plugin = "java")

    group = "net.globalcontrols"
    version = "1.0.0"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    repositories {
        mavenCentral()
    }
}

tasks.register("buildAll") {
    description = "Build all loader variants for all Minecraft versions"
    dependsOn(
        ":loader-forge:buildForgeAll",
        ":loader-fabric:buildFabricAll",
        ":loader-neoforge:buildNeoForgeAll",
        ":loader-quilt:buildQuiltAll"
    )
    group = "build"
}
