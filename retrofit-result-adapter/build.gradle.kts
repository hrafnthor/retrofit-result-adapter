plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.mavenPublish)
    checkstyle
}

dependencies {
    api(libs.bundles.network)
    api(libs.michaelbull.result)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        if (System.getenv("IS_CI") == "true") {
            events("failed", "skipped", "passed")
        }
        setExceptionFormat("full")
    }
}

tasks.register("listPropertiesAndPublish") {
    group = "publishing"

    val username = project.property("mavenCentralUsername")?.toString() ?: ""
    if (username.isNotEmpty()) {
        println("found maven central username $username")
    }

    finalizedBy(tasks.getByName("publish"))
}