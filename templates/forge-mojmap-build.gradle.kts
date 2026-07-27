plugins {
    id("xyz.wagyourtail.unimined")
    id("com.gradleup.shadow") version "8.3.0"{downgrade_plugin}
}{java_block}

dependencies {
    implementation(project(":common"))
    implementation(project(":platform-api"))
{platform_dep}}

unimined.minecraft {
    version("{mc}")
    mappings {
        mojmap()
    }
    minecraftForge {
        loader("{loader}")
    }
    defaultRemapJar = false
}

tasks.shadowJar {
    configurations = listOf(project.configurations.runtimeClasspath.get())
    archiveClassifier.set("")
    // These are provided by Minecraft — don't bundle them
    exclude("com/mojang/brigadier/**")
    exclude("com/google/gson/**")
    exclude("com/google/errorprone/**")
}

tasks.named("jar") {
    dependsOn(tasks.named("shadowJar"))
    enabled = false
}{downgrade_tasks}

sourceSets.main.get().java.srcDir(rootProject.file("src/shared/{shared}/java"))
