plugins {
    `kotlin-dsl`
    checkstyle
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}


dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.7.10")
}