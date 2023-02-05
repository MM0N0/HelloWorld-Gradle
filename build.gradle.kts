// used Gradle 7.6
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

plugins {
    id("java")
}

val versionPropPath = "version.properties"
val versionProp = initVersionProp()
version = versionProp.getProperty("version")

var tmpArtifactName = "HelloWorld-Gradle-$version.jar"
var artifactName = "HelloWorld-$version.jar"

group = "testpackage.test"
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
    // rename Artifact
    doLast {
        project.exec {
            val currDirecory = System.getProperty("user.dir")
            commandLine("bash", "-c", "cd $currDirecory/build/libs/ && mv $tmpArtifactName $artifactName")
        }
    }
}

// load Version
fun initVersionProp():Properties {
    return Properties().apply {
        load(FileInputStream(versionPropPath))
    }
}

// set Version
tasks.register("setVersion") {
    val version = System.getProperty("version")
    versionProp.setProperty("version", version)
    versionProp.store(FileOutputStream(versionPropPath), null)
}

// get Version
tasks.register("getVersion"){
    println(version)
}

// get Version, but cut off Snapshot (Ex.: 1.1.5-SNAPSHOT - > 1.1.5)
tasks.register("getVersionWithoutSnapshot"){
    project.exec {
        commandLine("bash", "-c", "echo $version | sed -r 's/([^-]*)-.*/\\1/g'")
    }
}

// get Version, but cut off Patch-Version (Ex.: 0.2.1 -> 0.2)
tasks.register("getReleaseBranchName"){
    project.exec {
        commandLine("bash", "-c", "echo $version | sed -r 's/([^\\.]*\\.[^\\.]*)\\..*/\\1/g'")
    }
}