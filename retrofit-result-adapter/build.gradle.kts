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

fun findAndPrintLength(key: String) {
    project.property(key)?.toString()?.let {
        println("found $key and its value is of length ${it.length}")
    }
}

tasks.register("listPropertiesAndPublish") {
    group = "publishing"

    findAndPrintLength("mavenCentralUsername")
    findAndPrintLength("mavenCentralPassword")
    findAndPrintLength("signingInMemoryKeyPassword")
    findAndPrintLength("signingInMemoryKeyId")
    findAndPrintLength("signingInMemoryKey")

    finalizedBy(tasks.getByName("publish"))
}