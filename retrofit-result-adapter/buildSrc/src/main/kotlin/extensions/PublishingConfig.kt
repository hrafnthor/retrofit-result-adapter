package extensions


@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class ScopeDsl

@ScopeDsl
interface Scope<T> {

    fun build(): T
}

data class PublishingConfig(
    var groupId: String,
    var artifactId: String,
    var version: String,
    var repositories: Set<Repository>,
    var pom: ProjectObjectModel,
    var signing: Signing,
)

class PublishingConfigScope(
    var groupId: String = "",
    var artifactId: String = "",
    var version: String = "",
    private var repositories: RepositorySetScope = RepositorySetScope(),
    private var pom: ProjectObjectModelScope = ProjectObjectModelScope(),
    private var signing: SigningScope = SigningScope(),
) : Scope<PublishingConfig> {

    override fun build(): PublishingConfig = PublishingConfig(
        groupId = groupId,
        artifactId = artifactId,
        version = version,
        repositories = repositories.build(),
        pom = pom.build(),
        signing = signing.build(),
    )

    fun repositories(init: RepositorySetScope.() -> Unit): Unit = init(this.repositories)

    fun pom(init: ProjectObjectModelScope.() -> Unit): Unit = init(this.pom)

    fun signing(init: SigningScope.() -> Unit): Unit = init(this.signing)
}


data class ProjectObjectModel(
    val name: String,
    val description: String,
    val url: String,
    val licenses: Set<License>,
    val developers: Set<Developer>,
    val sourceControlManagement: SourceControlManagement,
)

class ProjectObjectModelScope(
    var name: String = "",
    var description: String = "",
    var url: String = "",
    private val licenses: LicenseSetScope = LicenseSetScope(),
    private val developers: DeveloperSetScope = DeveloperSetScope(),
    private var sourceControlManagement: SourceControlManagementScope = SourceControlManagementScope()
) : Scope<ProjectObjectModel> {

    override fun build(): ProjectObjectModel = ProjectObjectModel(
        name = name,
        description = description,
        url = url,
        licenses = licenses.build(),
        developers = developers.build(),
        sourceControlManagement = sourceControlManagement.build()
    )

    fun developers(init: DeveloperSetScope.() -> Unit): Unit = init(developers)

    fun licenses(init: LicenseSetScope.() -> Unit): Unit = init(licenses)

    fun scm(init: SourceControlManagementScope.() -> Unit): Unit = init(sourceControlManagement)
}

abstract class SetScope<T>(
    internal val set: MutableSet<T> = mutableSetOf()
) : Scope<MutableSet<T>> {
    override fun build(): MutableSet<T> = set
}

class LicenseSetScope : SetScope<License>() {
    fun license(init: LicenseScope.() -> Unit) {
        this.set.add(LicenseScope().apply(init).build())
    }
}

class DeveloperSetScope : SetScope<Developer>() {
    fun developer(init: DeveloperScope.() -> Unit) {
        this.set.add(DeveloperScope().apply(init).build())
    }
}

class RepositorySetScope : SetScope<Repository>() {
    fun repository(init: RepositoryScope.() -> Unit) {
        this.set.add(RepositoryScope().apply(init).build())
    }
}

data class License(
    val name: String = "",
    val url: String = "",
    val comments: String = "",
    val distribution: String = "",
)

class LicenseScope(
    var name: String = "",
    var url: String = "",
    var comments: String = "",
    var distribution: String = "",
) : Scope<License> {
    override fun build(): License = License(
        name = name,
        url = url,
        comments = comments,
        distribution = distribution
    )
}

data class Developer(
    val name: String,
    val id: String,
    val url: String,
    val email: String,
    val organization: String,
)

class DeveloperScope(
    var name: String = "",
    var id: String = "",
    var url: String = "",
    var email: String = "",
    var organization: String = ""
) : Scope<Developer> {
    override fun build(): Developer = Developer(
        name = name,
        id = id,
        url = url,
        email = email,
        organization = organization
    )
}

data class SourceControlManagement(
    val connection: String = "",
    val developerConnection: String = "",
    val url: String = "",
)

class SourceControlManagementScope(
    var connection: String = "",
    var developerConnection: String = "",
    var url: String = "",
) : Scope<SourceControlManagement> {
    override fun build(): SourceControlManagement = SourceControlManagement(
        connection = connection,
        developerConnection = developerConnection,
        url = url
    )
}

data class Signing(
    val keyId: String = "",
    val password: String = "",
    val key: String = ""
)

class SigningScope(
    var keyId: String = "",
    var password: String = "",
    var key: String = "",
) : Scope<Signing> {
    override fun build(): Signing = Signing(keyId = keyId, password = password, key = key)
}

data class Repository(
    val username: String,
    val password: String,
    val url: String,
    val name: String
)

class RepositoryScope(
    var username: String = "",
    var password: String = "",
    var url: String = "",
    var name: String = ""
) : Scope<Repository> {
    override fun build(): Repository = Repository(
        username = username,
        password = password,
        url = url,
        name = name
    )
}