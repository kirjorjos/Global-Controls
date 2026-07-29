repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":platform-api"))
    implementation("com.google.code.gson:gson:2.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

sourceSets.test.get().java.srcDir("test/java")

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    expand(mapOf("version" to version))
}
