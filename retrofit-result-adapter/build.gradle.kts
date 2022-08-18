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
        if (System.getenv("CI") == "true") {
            events("failed", "skipped", "passed")
        }
        setExceptionFormat("full")
    }
}