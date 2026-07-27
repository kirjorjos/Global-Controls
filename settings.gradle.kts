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

include(":common")
project(":common").projectDir = file("src/common")

include(":platform-api")
project(":platform-api").projectDir = file("src/platform-api")

include(":platform-legacy")
project(":platform-legacy").projectDir = file("src/platform-legacy")

include(":platform-brigadier")
project(":platform-brigadier").projectDir = file("src/platform-brigadier")

include(":forge-1_7_10")
project(":forge-1_7_10").projectDir = file("src/forge/forge-1_7_10")

include(":forge-1_12_2")
project(":forge-1_12_2").projectDir = file("src/forge/forge-1_12_2")

include(":forge-1_14_4")
project(":forge-1_14_4").projectDir = file("src/forge/forge-1_14_4")

include(":forge-1_15_2")
project(":forge-1_15_2").projectDir = file("src/forge/forge-1_15_2")

include(":forge-1_16_5")
project(":forge-1_16_5").projectDir = file("src/forge/forge-1_16_5")

include(":forge-1_17_1")
project(":forge-1_17_1").projectDir = file("src/forge/forge-1_17_1")

include(":forge-1_18_2")
project(":forge-1_18_2").projectDir = file("src/forge/forge-1_18_2")

include(":forge-1_19_4")
project(":forge-1_19_4").projectDir = file("src/forge/forge-1_19_4")

include(":forge-1_20_1")
project(":forge-1_20_1").projectDir = file("src/forge/forge-1_20_1")

include(":forge-1_20_2")
project(":forge-1_20_2").projectDir = file("src/forge/forge-1_20_2")

include(":forge-1_20_4")
project(":forge-1_20_4").projectDir = file("src/forge/forge-1_20_4")

include(":fabric-1_14_4")
project(":fabric-1_14_4").projectDir = file("src/fabric/fabric-1_14_4")

include(":fabric-1_15_2")
project(":fabric-1_15_2").projectDir = file("src/fabric/fabric-1_15_2")

include(":fabric-1_16_5")
project(":fabric-1_16_5").projectDir = file("src/fabric/fabric-1_16_5")

include(":fabric-1_17_1")
project(":fabric-1_17_1").projectDir = file("src/fabric/fabric-1_17_1")

include(":fabric-1_18_2")
project(":fabric-1_18_2").projectDir = file("src/fabric/fabric-1_18_2")

include(":fabric-1_19_4")
project(":fabric-1_19_4").projectDir = file("src/fabric/fabric-1_19_4")

include(":fabric-1_20_1")
project(":fabric-1_20_1").projectDir = file("src/fabric/fabric-1_20_1")

include(":fabric-1_20_2")
project(":fabric-1_20_2").projectDir = file("src/fabric/fabric-1_20_2")

include(":fabric-1_20_4")
project(":fabric-1_20_4").projectDir = file("src/fabric/fabric-1_20_4")

include(":fabric-1_21")
project(":fabric-1_21").projectDir = file("src/fabric/fabric-1_21")

include(":quilt-1_18_2")
project(":quilt-1_18_2").projectDir = file("src/quilt/quilt-1_18_2")

include(":quilt-1_19_4")
project(":quilt-1_19_4").projectDir = file("src/quilt/quilt-1_19_4")

include(":quilt-1_21")
project(":quilt-1_21").projectDir = file("src/quilt/quilt-1_21")

include(":neoforge-1_20_2")
project(":neoforge-1_20_2").projectDir = file("src/neoforge/neoforge-1_20_2")

include(":neoforge-1_20_4")
project(":neoforge-1_20_4").projectDir = file("src/neoforge/neoforge-1_20_4")

include(":neoforge-1_21")
project(":neoforge-1_21").projectDir = file("src/neoforge/neoforge-1_21")
