repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(project(":platform-api"))
    implementation(project(":common"))
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.mojang:brigadier:1.2.9")
}
