import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    checkstyle
}

allprojects {

    repositories {
        mavenCentral()
    }
    tasks.withType(JavaCompile::class).configureEach {
        options.encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }
    tasks.withType(KotlinCompile::class).configureEach {
        kotlinOptions {
            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }
}
