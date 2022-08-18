import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.mavenPublish)
    checkstyle
}

allprojects {
    repositories {
        mavenCentral()
    }
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // Treat all Kotlin warnings as errors
//      allWarningsAsErrors = true

        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = freeCompilerArgs + "-Xexplicit-api=strict"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

tasks.withType<Jar> {
    from(sourceSets["main"].allSource)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}