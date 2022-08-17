package extensions

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property


@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
public annotation class BuilderDsl

data class PublishingConfig(
    var groupId: String,
    var artifactId: String,
    var version: String,
    var pom: ProjectObjectModel,
    var signing: Signing,
) {
    @BuilderDsl
    class Builder(
        var groupId: String = "",
        var artifactId: String = "",
        var version: String = "",
        var pom: ProjectObjectModel.Builder = ProjectObjectModel.Builder(),
        var signing: Signing.Builder = Signing.Builder(),
    ) {

        fun build(): PublishingConfig = PublishingConfig(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            pom = pom.build(),
            signing = signing.build(),
        )
    }
}

inline fun PublishingConfig.Builder.pom(init: ProjectObjectModel.Builder.() -> Unit): Unit = init(this.pom)

inline fun PublishingConfig.Builder.signing(init: Signing.Builder.() -> Unit): Unit = init(this.signing)

data class ProjectObjectModel(
    val name: String,
    val description: String,
    val url: String,
    val licenses: Set<License>,
    val developers: Set<Developer>,
    val sourceControlManagement: SourceControlManagement,
) {

    @BuilderDsl
    class Builder(
        var name: String = "",
        var description: String = "",
        var url: String = "",
        val licenses: LicenseSet = SetBuilder(),
        val developers: DeveloperSet = SetBuilder(),
        var sourceControlManagement: SourceControlManagement.Builder = SourceControlManagement.Builder()
    ) {

        fun build(): ProjectObjectModel = ProjectObjectModel(
            name = name,
            description = description,
            url = url,
            licenses = licenses.set,
            developers = developers.set,
            sourceControlManagement = sourceControlManagement.build()
        )
    }
}

inline fun ProjectObjectModel.Builder.developers(init: DeveloperSet.() -> Unit): Unit = init(developers)

inline fun ProjectObjectModel.Builder.licenses(init: LicenseSet.() -> Unit): Unit = init(licenses)

inline fun ProjectObjectModel.Builder.scm(init: SourceControlManagement.Builder.() -> Unit): Unit = init(sourceControlManagement)

@BuilderDsl
open class SetBuilder<T>(
    val set: MutableSet<T> = mutableSetOf()
)

typealias LicenseSet = SetBuilder<License>

typealias DeveloperSet = SetBuilder<Developer>

inline fun LicenseSet.license(init: License.Builder.() -> Unit) {
    this.set.add(License.Builder().apply(init).build())
}

inline fun DeveloperSet.developer(init: Developer.Builder.() -> Unit) {
    this.set.add(Developer.Builder().apply(init).build())
}

data class License(
    val name: String = "",
    val url: String = "",
    val comments: String = "",
    val distribution: String = "",
) {
    @BuilderDsl
    class Builder(
        var name: String = "",
        var url: String = "",
        var comments: String = "",
        var distribution: String = "",
    ) {
        fun build(): License = License(
            name = name,
            url = url,
            comments = comments,
            distribution = distribution
        )
    }
}

data class Developer(
    val name: String,
    val id: String,
    val url: String,
    val email: String,
    val organization: String,
) {
    @BuilderDsl
    class Builder(
        var name: String ="",
        var id: String ="",
        var url: String = "",
        var email: String = "",
        var organization: String = ""
    ) {
        fun build(): Developer = Developer(
            name = name,
            id = id,
            url = url,
            email = email,
            organization = organization
        )
    }
}

data class SourceControlManagement(
    val connection: String = "",
    val developerConnection: String = "",
    val url: String = "",
) {
    @BuilderDsl
    class Builder(
        var connection: String = "",
        var developerConnection: String = "",
        var url: String = "",
    ) {
        fun build(): SourceControlManagement = SourceControlManagement(
            connection = connection,
            developerConnection = developerConnection,
            url = url
        )
    }
}


data class Signing(
    val keyId: String = "",
    val password: String = "",
    val key: String = ""
) {
    @BuilderDsl
    class Builder(
        var keyId: String = "",
        var password: String = "",
        var key: String = "",
    ) {
        fun build(): Signing = Signing(keyId = keyId, password = password, key = key)
    }
}

open class Repository(objects: ObjectFactory) {

    private val _username: Property<String> = objects.property(String::class.java)
    var username: String
        get() = _username.get()
        set(value) {
            _username.set(value)
            _username.disallowChanges()
        }

    private val _password: Property<String> = objects.property(String::class.java)
    var password: String
        get() = _password.get()
        set(value) {
            _password.set(value)
            _password.disallowChanges()
        }


    private val _stagingProfileId: Property<String> = objects.property(String::class.java)
    var stagingProfileId: String
        get() = _stagingProfileId.get()
        set(value) {
            _stagingProfileId.set(value)
            _stagingProfileId.disallowChanges()
        }

    init {
        _username.set("")
        _password.set("")
        _stagingProfileId.set("")
    }
}