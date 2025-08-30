plugins {
    java
    kotlin("jvm") version "2.2.0-Beta2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "me.lukiiy"
version = "3.8-SNAPSHOT"
description = "A silly plugin that adds some cool useful things"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
    implementation(files("libs/Lecour.jar"))
}

tasks {
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
        minimize()
    }

    build { dependsOn(shadowJar) }

    processResources {
        val props = mapOf("version" to version)

        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") { expand(props) }
    }
}

kotlin.jvmToolchain(21)