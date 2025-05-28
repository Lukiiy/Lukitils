plugins {
    java
    kotlin("jvm") version "2.2.0-Beta2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "me.lukiiy"
version = "3.7"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
        relocate("kotlin", "me.lukiiy.utils.repkg.kt")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to version)

        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

val targetJava = 21
kotlin { jvmToolchain(targetJava) }