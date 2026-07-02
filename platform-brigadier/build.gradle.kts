repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":platform-api"))
    implementation(project(":common"))
    implementation("com.google.code.gson:gson:2.11.0")
}
