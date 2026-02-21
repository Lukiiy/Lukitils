plugins {
    java
    kotlin("jvm") version "2.3.10"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "me.lukiiy"
version = "3.8.1"
description = "A silly plugin that adds some cool useful things"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.viaversion.com")
}

dependencies {
    paperweight.paperDevBundle("1.21.9-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
    implementation(files("libs/Lecour.jar"))
    compileOnly("com.viaversion:viaversion-api:5.6.0")
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