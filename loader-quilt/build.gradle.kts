plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform-api"))
    implementation(project(":platform-brigadier"))
    compileOnly("org.quiltmc:quilt-loader:0.26.1")
}

unimined.minecraft {
    version("1.21")
    mappings {
        mojmap()
    }
    quilt {
        loader("0.26.1")
    }
    defaultRemapJar = false
    remap(tasks.shadowJar.get())
}

tasks.shadowJar {
    configurations = listOf(project.configurations.runtimeClasspath.get())
    archiveClassifier.set("")
}
