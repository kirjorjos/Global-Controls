pluginManagement {
    repositories {
        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.wagyourtail.xyz/snapshots")
        mavenCentral()
        gradlePluginPortal {
            content {
                excludeGroup("org.apache.logging.log4j")
            }
        }
    }
}

plugins {
    id("xyz.wagyourtail.unimined") version "1.4.1" apply false
}

rootProject.name = "GlobalControls"

include(
    "common",
    "platform-api",
    "platform-legacy",
    "platform-brigadier",
    "loader-forge",
    "loader-fabric",
    "loader-neoforge",
    "loader-quilt",
    "forge-1710",
    "forge-1122",
    "forge-1201",
    "fabric-1201",
    "fabric-121"
)
