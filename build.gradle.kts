// used Gradle 7.6

plugins {
    id("java")
}

group = "testpackage.test"
version = "1.0-SNAPSHOT"
var mainClass = "testpackage.test.App"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.tinylog:tinylog-api:2.3.2")
    implementation("org.tinylog:tinylog-impl:2.3.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// build executable jar
tasks.jar {
    manifest.attributes(mapOf(Pair("Main-Class", mainClass)))
}
// build fat jar
tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}
