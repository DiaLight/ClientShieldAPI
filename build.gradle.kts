import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
}

allprojects {

    apply(plugin = "java")
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") { name = "spigotmc-repo" }
        maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype-repo" }
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:13.0")
        compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

val processResources by tasks.getting(ProcessResources::class) {
    inputs.property("version", version)
    val sourceSets = java.run { sourceSets }
    val main by sourceSets.getting
    from(main.resources.srcDirs) {
        filter<ReplaceTokens>("tokens" to mapOf(
            "version" to version
        ))
    }
}


