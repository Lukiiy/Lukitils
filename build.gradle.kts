plugins {
    java
    kotlin("jvm") version "2.0.21"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "me.lukiiy"
version = "3.5"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
        relocate("kotlin", "me.lukiiy.utils.shadowedkt")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}

val targetJava = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJava)

    if (JavaVersion.current() < javaVersion) toolchain.languageVersion = JavaLanguageVersion.of(targetJava)
}

val encoding = "UTF-8"
tasks.withType<JavaCompile> {
    options.encoding = encoding

    if (targetJava >= 10 || JavaVersion.current().isJava10Compatible()) options.release.set(targetJava)
}

tasks.processResources {
    val props = mapOf("version" to version)

    inputs.properties(props)
    filteringCharset = encoding
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}


kotlin {
    jvmToolchain(targetJava)
}