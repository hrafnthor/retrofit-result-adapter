package extensions

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject


open class PublishModuleExtension @Inject constructor(private val objects: ObjectFactory) {

    private val _groupId: Property<String> = objects.property(String::class.java)
    var groupId: String
        get() = _groupId.get()
        set(value) {
            _groupId.set(value)
            _groupId.disallowChanges()
        }

    private val _artifactId: Property<String> = objects.property(String::class.java)
    var artifactId: String
        get() = _artifactId.get()
        set(value) {
            _artifactId.set(value)
            _artifactId.disallowChanges()
        }

    private val _version: Property<String> = objects.property(String::class.java)
    var version: String
        get() = _version.get()
        set(value) {
            _version.set(value)
            _version.disallowChanges()
        }

    private val _pom: Property<Pom> = objects.property(Pom::class.java)
    var pom: Pom
        get() = _pom.get()
        set(value) {
            _pom.set(value)
            _pom.disallowChanges()
        }

    private val _signing: Property<Signing> = objects.property(Signing::class.java)
    var signing: Signing
        get() = _signing.get()
        set(value) {
            _signing.set(value)
            _signing.disallowChanges()
        }

    init {
        _groupId.set("")
        _artifactId.set("")
        _pom.set(Pom(objects))
        _signing.set(Signing(objects))
    }
}

open class Pom(private val objects: ObjectFactory) {

    private val _name: Property<String> = objects.property(String::class.java)
    var name: String
        get() = _name.get()
        set(value) {
            _name.set(value)
            _name.disallowChanges()
        }

    private val _description: Property<String> = objects.property(String::class.java)
    var description: String
        get() = _description.get()
        set(value) {
            _description.set(value)
            _description.disallowChanges()
        }

    private val _url: Property<String> = objects.property(String::class.java)
    var url: String
        get() = _url.get()
        set(value) {
            _url.set(value)
            _url.disallowChanges()
        }

    private val _licenses: MutableList<License> = mutableListOf()
    val licenses: List<License>
        get() = _licenses

    private val _developers: MutableList<Developer> = mutableListOf()
    val developers: List<Developer>
        get() = _developers

    val scm: SourceControlManagement = SourceControlManagement(objects)

    init {
        _name.set("")
        _description.set("")
        _url.set("")
    }

    fun license(block: License.() -> Unit) {
        val license = License(objects)
        block(license)
        _licenses.add(license)
    }

    fun developer(block: Developer.() -> Unit) {
        val developer = Developer(objects)
        block(developer)
        _developers.add(developer)
    }

    fun scm(block: SourceControlManagement.() -> Unit): Unit = block(scm)


}

open class License(objects: ObjectFactory) {

    private val _name: Property<String> = objects.property(String::class.java)
    var name: String
        get() = _name.get()
        set(value) {
            _name.set(value)
            _name.disallowChanges()
        }

    private val _url: Property<String> = objects.property(String::class.java)
    var url: String
        get() = _url.get()
        set(value) {
            _url.set(value)
            _url.disallowChanges()
        }

    private val _comments: Property<String> = objects.property(String::class.java)
    var comments: String
        get() = _comments.get()
        set(value) {
            _comments.set(value)
            _comments.disallowChanges()
        }

    private val _distribution: Property<String> = objects.property(String::class.java)
    var distribution: String
        get() = _distribution.get()
        set(value) {
            _distribution.set(value)
            _distribution.disallowChanges()
        }

    init {
        _name.set("")
        _url.set("")
        _comments.set("")
        _distribution.set("")
    }
}

open class Developer(objects: ObjectFactory) {

    private val _name: Property<String> = objects.property(String::class.java)
    var name: String
        get() = _name.get()
        set(value) {
            _name.set(value)
            _name.disallowChanges()
        }

    private val _id: Property<String> = objects.property(String::class.java)
    var id: String
        get() = _id.get()
        set(value) {
            _id.set(value)
            _id.disallowChanges()
        }

    private val _url: Property<String> = objects.property(String::class.java)
    var url: String
        get() = _url.get()
        set(value) {
            _url.set(value)
            _url.disallowChanges()
        }

    private val _email: Property<String> = objects.property(String::class.java)
    var email: String
        get() = _email.get()
        set(value) {
            _email.set(value)
            _email.disallowChanges()
        }

    private val _organization: Property<String> = objects.property(String::class.java)
    var organization: String
        get() = _organization.get()
        set(value) {
            _organization.set(value)
            _organization.disallowChanges()
        }

    init {
        _name.set("")
        _id.set("")
        _url.set("")
        _email.set("")
        _organization.set("")
    }
}

open class SourceControlManagement(objects: ObjectFactory) {

    private val _connection: Property<String> = objects.property(String::class.java)
    var connection: String
        get() = _connection.get()
        set(value) {
            _connection.set(value)
            _connection.disallowChanges()
        }

    private val _developerConnection: Property<String> = objects.property(String::class.java)
    var developerConnection: String
        get() = _developerConnection.get()
        set(value) {
            _developerConnection.set(value)
            _developerConnection.disallowChanges()
        }

    private val _url: Property<String> = objects.property(String::class.java)
    var url: String
        get() = _url.get()
        set(value) {
            _url.set(value)
            _url.disallowChanges()
        }

    init {
        _connection.set("")
        _developerConnection.set("")
        _url.set("")
    }
}

open class Signing(objects: ObjectFactory) {
    private val _keyId: Property<String> = objects.property(String::class.java)
    var keyId: String
        get() = _keyId.get()
        set(value) {
            _keyId.set(value)
            _keyId.disallowChanges()
        }

    private val _password: Property<String> = objects.property(String::class.java)
    var password: String
        get() = _password.get()
        set(value) {
            _password.set(value)
            _password.disallowChanges()
        }

    private val _key: Property<String> = objects.property(String::class.java)
    var key: String
        get() = _key.get()
        set(value) {
            _key.set(value)
            _key.disallowChanges()
        }

    init {
        _keyId.set("")
        _password.set("")
        _key.set("")
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