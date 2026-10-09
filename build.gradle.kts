import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "2.3.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    `maven-publish`
}

group = "io.github.team-sneakymouse"

// Requires a git checkout with at least one commit (CI and local clones).
version =
    providers.exec {
        workingDir(rootDir)
        commandLine("git", "show", "-s", "--format=%ct:%h", "--abbrev=12", "HEAD")
    }.standardOutput.asText.map { commit ->
        val (timestamp, hash) = commit.trim().split(":", limit = 2)
        val date =
            DateTimeFormatter.ofPattern("yyyy.MM.dd").withZone(ZoneOffset.UTC)
                .format(Instant.ofEpochSecond(timestamp.toLong()))
        "$date-$hash"
    }.get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    // Internal Team-Sneakymouse / Lords of Minecraft artifacts:
    // maven("https://maven.sneakyrp.com/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.3.21")
    // Example internal dependency (uncomment sneakyrp repo above when needed):
    // compileOnly("io.github.team-sneakymouse:sneakypocketbase-api:VERSION")
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
    }
}

tasks {
    processResources {
        inputs.property("version", project.version.toString())
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version.toString())
        }
    }

    jar {
        archiveBaseName.set(rootProject.name)
        archiveFileName.set("${rootProject.name}-${project.version}.jar")
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    runServer {
        minecraftVersion("26.2")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "emptyplugin"
            from(components["java"])
            pom {
                name.set("EmptyPlugin")
                description.set(
                    "Standard Paper plugin template for Team-Sneakymouse / Lords of Minecraft."
                )
                url.set("https://github.com/Team-Sneakymouse/EmptyPlugin")
                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("team-sneakymouse")
                        name.set("Team Sneakymouse")
                        url.set("https://github.com/Team-Sneakymouse")
                    }
                }
                scm {
                    url.set("https://github.com/Team-Sneakymouse/EmptyPlugin")
                    connection.set("scm:git:https://github.com/Team-Sneakymouse/EmptyPlugin.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "sneakyrp"
            url = uri("https://maven.sneakyrp.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<org.gradle.authentication.http.BasicAuthentication>("basic")
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.check)
}
