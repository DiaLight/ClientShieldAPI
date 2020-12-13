import org.apache.tools.ant.filters.ReplaceTokens


plugins {
    java
}

dependencies {
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
