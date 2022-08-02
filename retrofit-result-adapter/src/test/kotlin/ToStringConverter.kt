import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.jetbrains.annotations.Nullable
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class ToStringConverterFactory : Converter.Factory() {
    @Nullable
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, String>? {
        return if (String::class.java == type) {
            Converter<ResponseBody, String> {
                // This is done for testing purposes else empty body will be empty
                // string rather than null as expected inside ResultCall
                if (it.contentLength() > 0) {
                    it.string()
                } else null
            }
        } else null
    }

    @Nullable
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<String, RequestBody>? {
        return if (String::class.java == type) {
            Converter { it.toRequestBody(MEDIA_TYPE) }
        } else null
    }

    companion object {
        val MEDIA_TYPE: MediaType = "text/plain".toMediaType()
    }
}