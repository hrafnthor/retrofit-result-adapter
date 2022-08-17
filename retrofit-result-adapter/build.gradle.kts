
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm") version "1.7.10"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    `publish-module`
    checkstyle
}

group = "is.hth"

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    api(libs.bundles.network)
    api(libs.michaelbull.result)

    testImplementation(kotlin("test"))
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

val localProps = project.rootProject.file("local.properties")
val properties: Properties = if (localProps.exists()) {
    Properties().also { properties ->
        FileInputStream(localProps).bufferedReader().use { properties.load(it) }
    }
} else {
    val env = System.getenv()
    Properties().apply {
        setProperty("sonatypeStagingProfileId", env["SONATYPE_STAGING_PROFILE_ID"])
        setProperty("ossrhUsername", env["OSSRH_USERNAME"])
        setProperty("ossrhPassword", env["OSSRH_PASSPHRASE"])
        setProperty("signingKey", env["GPG_SIGNING_KEY"])
        setProperty("signingKeyId", env["GPG_SIGNING_KEY_ID"])
        setProperty("signingKeyPassword", env["GPG_PASSPHRASE"])
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(properties.getProperty("ossrhUsername"))
            password.set(properties.getProperty("ossrhPassword"))
            stagingProfileId.set(properties.getProperty("sonatypeStagingProfileId"))

            // Different nexus url requirements for signups made post 24th of February 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishModule {
    configure {
        groupId = group.toString()
        artifactId = "retrofit-result-adapter"
        version = project.version.toString()

        pom {
            name = "retrofit-result-adapter"
            description = "Retrofit response handling for Result monads"
            url = "https://github.com/hrafnthor/retrofit-result-adapter"

            licenses {
                license {
                    name = "MIT License"
                    url = "https://opensource.org/licenses/MIT"
                }
            }
            developers {
                developer {
                    id = "hrafnth"
                    name = "Hrafn Thorvaldsson"
                    email = "hrafn@hth.is"
                }
            }

            scm {
                connection = "scm:git:git://github.com/hrafnthor/retrofit-result-adapter.git"
                developerConnection = "scm:git:ssh://github.com/hrafnthor/retrofit-result-adapter.git"
                url = "https://github.com/hrafnthor/retrofit-result-adapter"
            }
        }

        signing {
            keyId = properties.getProperty("signingKeyId")
            key = properties.getProperty("signingKey")
            password = properties.getProperty("signingKeyPassword")
        }
    }
}
