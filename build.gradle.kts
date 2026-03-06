plugins {
    java
    jacoco
    `maven-publish`
}

group = "com.github.lukesky19"
version = "0.2.0.0"

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.lukesky19:SkyLib:1.5.0.0")

    // Hooks
    compileOnly("world.bentobox:bentobox:2.7.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.14")

    // Test Dependencies
    testImplementation("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT") {
        exclude("com.google.code.gson", "gson")
    }
    testImplementation("com.github.lukesky19:SkyLib:1.5.0.0")
    testImplementation("world.bentobox:bentobox:2.7.0-SNAPSHOT")
    testImplementation("com.sk89q.worldguard:worldguard-bukkit:7.0.15") {
        exclude("com.google.code.gson", "gson")
    }

    testImplementation("org.xerial:sqlite-jdbc:3.51.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.14.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.98.4")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    javadoc {
        source = sourceSets["main"].allJava
        classpath = files() + configurations["compileClasspath"]

        (options as StandardJavadocDocletOptions).apply {
            tags("apiNote:a:API Note:")
            addStringOption("sourcepath", "")
        }
    }

    test {
        useJUnitPlatform()

        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)

        reports {
            xml.required = false
            csv.required = false
            html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
        }
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveClassifier.set("")
    }

    build {
        dependsOn(javadoc)
        dependsOn(publishToMavenLocal)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}