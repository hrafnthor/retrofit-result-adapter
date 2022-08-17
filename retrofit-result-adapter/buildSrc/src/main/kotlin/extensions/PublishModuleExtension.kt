import extensions.PublishingConfig
import extensions.PublishingConfigScope
import extensions.ScopeDsl
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@ScopeDsl
open class PublishModuleExtension @Inject constructor(objects: ObjectFactory) {

    private val _config: Property<PublishingConfig> = objects.property(PublishingConfig::class.java)
    var config: PublishingConfig
        get() = _config.get()
        private set(value) {
            _config.set(value)
            _config.disallowChanges()
        }

    init {
        _config.set(PublishingConfigScope().build())
    }

    fun configure(init: PublishingConfigScope.() -> Unit) {
        this.config = PublishingConfigScope().apply(init).build()
    }
}