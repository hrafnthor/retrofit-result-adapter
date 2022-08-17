import org.gradle.kotlin.dsl.signing

plugins {
    id("maven-publish")
    id("org.jetbrains.dokka")
    signing
    `java-library`
}

java {
    withJavadocJar()
    withSourcesJar()
}

project.extensions.create("publishModule", PublishModuleExtension::class)

afterEvaluate {
    val config = project.extensions.getByType(PublishModuleExtension::class).config

    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = config.groupId
                artifactId = config.artifactId
                version = config.version

                pom {
                    val pom = config.pom
                    name.set(pom.name)
                    description.set(pom.description)
                    url.set(pom.url)

                    licenses {
                        pom.licenses.forEach { license ->
                            license {
                                name.set(license.name)
                                url.set(license.url)
                                comments.set(license.comments)
                                distribution.set(license.distribution)
                            }
                        }
                    }

                    developers {
                        pom.developers.forEach { developer ->
                            developer {
                                id.set(developer.id)
                                name.set(developer.name)
                                email.set(developer.email)
                                url.set(developer.url)
                                organization.set(developer.organization)
                            }
                        }
                    }

                    scm {
                        val scm = pom.sourceControlManagement
                        connection.set(scm.connection)
                        developerConnection.set(scm.developerConnection)
                        url.set(scm.url)
                    }
                }
                from(components["java"])
            }
        }
    }

    signing {
        val signing = config.signing
        useInMemoryPgpKeys(
            signing.keyId,
            signing.key,
            signing.password
        )

        sign(publishing.publications)
    }
}
