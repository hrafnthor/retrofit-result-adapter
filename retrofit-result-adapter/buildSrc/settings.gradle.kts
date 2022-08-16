// Gradle's buildSrc does not automatically gain access to the generated dependency catalog
// since 'buildSrc' is considered to be a completely different project in the eyes of Gradle.
// So the catalog needs to be generated specifically for 'buildSrc'
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

