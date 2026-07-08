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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "GlobalControls"

include(
    "common",
    "platform-api",
    "platform-legacy",
    "platform-brigadier",
    "forge-1710",
    "forge-1122",
    "forge-1144",
    "forge-1152",
    "forge-1165",
    "forge-1171",
    "forge-1182",
    "forge-1194",
    "forge-1201",
    "forge-1202",
    "forge-1204",
    "fabric-1144",
    "fabric-1152",
    "fabric-1165",
    "fabric-1171",
    "fabric-1182",
    "fabric-1194",
    "fabric-1201",
    "fabric-1202",
    "fabric-1204",
    "fabric-121",
    "quilt-1182",
    "quilt-1194",
    "quilt-121",
    "neoforge-1202",
    "neoforge-1204",
    "neoforge-121"
)
