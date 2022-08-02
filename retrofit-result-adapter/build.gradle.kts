import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    alias(libs.plugins.spotless)
}

group = "is.hth"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(libs.bundles.network)
    implementation(libs.michaelbull.result)

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // Treat all Kotlin warnings as errors
//                allWarningsAsErrors = true

        // Set JVM target to 11
        jvmTarget = JavaVersion.VERSION_11.toString()

        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()

        kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}