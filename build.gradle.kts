plugins {
    java
    id("xyz.wagyourtail.unimined") version "1.4.1" apply false
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
        ":forge-1710:build",
        ":forge-1122:build",
        ":forge-1201:build",
        ":fabric-1201:build",
        ":fabric-121:build",
        ":loader-neoforge:build",
        ":loader-quilt:build"
    )
    group = "build"
}
