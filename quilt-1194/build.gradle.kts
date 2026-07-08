plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    compileOnly("org.quiltmc:quilt-loader:0.26.1")
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-brigadier"))
}

repositories {
    unimined.quiltMaven()
}

unimined.minecraft {
    version("1.19.4")
    mappings {
        mojmap()
    }
    quilt {
        loader("0.26.1")
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

tasks.named("assemble") {
    dependsOn(tasks.named("shadowJar"))
}


sourceSets.main.get().java.srcDir(rootProject.file("shared/quilt/src/main/java"))
