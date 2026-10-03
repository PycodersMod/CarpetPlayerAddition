import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
import java.nio.charset.StandardCharsets
import java.util.Base64

plugins {
    id("fabric-loom") version "1.10.5"
    `maven-publish`
}

val mod_version: String by project
val maven_group: String by project
val archives_base_name: String by project
val minecraft_version: String by project
val loader_version: String by project
val fabric_api_version: String by project
val carpet_version: String by project
val pycodersRuntimeProjectId = providers.gradleProperty("pycodersRuntimeProjectId").orElse(rootProject.name).get()
val pycodersConfiguredRunDir = providers.gradleProperty("pycodersRuntimeDir").orNull?.let { file(it).canonicalFile }
val pycodersConfiguredRuntimeRoot = providers.gradleProperty("pycodersRuntimeRoot").orNull
    ?: providers.environmentVariable("MMTL_WORKSPACE_RUNTIME_ROOT").orNull
val pycodersConfiguredRootRunDir = pycodersConfiguredRuntimeRoot?.let { File(it, "legacy-import/$pycodersRuntimeProjectId/run").canonicalFile }
val pycodersDiscoveredRunDir = generateSequence(project.projectDir.canonicalFile) { it.parentFile }
    .map { File(it, "runtime/legacy-import/$pycodersRuntimeProjectId/run").canonicalFile }
    .firstOrNull { it.isDirectory }
val pycodersRunDir = pycodersConfiguredRunDir ?: pycodersConfiguredRootRunDir ?: pycodersDiscoveredRunDir ?: file("run").canonicalFile
fun decodeArgs(name: String): List<String> = providers.gradleProperty(name).orNull?.takeIf { it.isNotEmpty() }?.split('.')?.map { if (it == "_") "" else String(Base64.getDecoder().decode(it), StandardCharsets.UTF_8) } ?: emptyList()
val pycodersGameArgs = decodeArgs("pycodersGameArgsB64")
val pycodersJavaArgs = decodeArgs("pycodersJavaArgsB64")
val pycodersUsername = providers.gradleProperty("pycodersUsername").orElse("Dev").get()

version = mod_version
group = maven_group

base {
    archivesName.set(archives_base_name)
}

loom {
    runs {
        configureEach {
            runDir(project.relativePath(pycodersRunDir))
            pycodersJavaArgs.forEach { vmArg(it) }
            pycodersGameArgs.forEach { programArg(it) }
        }
        named("client") {
            programArg("--username")
            programArg(pycodersUsername)
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
    modImplementation("maven.modrinth:carpet:$carpet_version")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = archives_base_name
            from(components["java"])
        }
    }
}
