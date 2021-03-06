import org.apache.tools.ant.filters.ReplaceTokens


plugins {
    java
}
repositories {
    maven("https://repo.codemc.org/repository/maven-public/") { name = "codemc-repo" }
}
dependencies {
    compileOnly("fr.xephi:authme:5.6.0-SNAPSHOT")
    compileOnly(parent!!.parent!!)
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
