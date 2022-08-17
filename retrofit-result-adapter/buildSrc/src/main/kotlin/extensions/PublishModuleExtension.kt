import extensions.BuilderDsl
import extensions.PublishingConfig
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class PublishModuleExtension @Inject constructor(objects: ObjectFactory) {

    private val _config: Property<PublishingConfig> = objects.property(PublishingConfig::class.java)
    var config: PublishingConfig
        get() = _config.get()
        set(value) {
            _config.set(value)
            _config.disallowChanges()
        }

    init {
        _config.set(PublishingConfig.Builder().build())
    }
}

inline fun PublishModuleExtension.configure(init: PublishingConfig.Builder.() -> Unit) {
    this.config = PublishingConfig.Builder().apply(init).build()
}
