import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

internal class AdapterTest : StringSpec({

    val server = autoClose(MockWebServer())

    "expecting a body and receiving it returns Ok(T)" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        val payload = "testing"
        server.enqueue(MockResponse().setResponseCode(200).setBody(payload))

        val result = runBlocking { service.body() }
        result shouldBe Ok(payload)
    }

    "expecting a body but receiving none returns Err()" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        server.enqueue(MockResponse().setResponseCode(200))

        val result = runBlocking { service.body() }
        result shouldBe Err(Processor.emptyError)
    }

    "expecting no body and receiving none returns Ok(Unit)" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        server.enqueue(MockResponse().setResponseCode(200))

        val result = runBlocking { service.unit() }
        result shouldBe Ok(Unit)
    }

    "expecting no body but receiving one returns Ok(Unit)" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        server.enqueue(MockResponse().setResponseCode(200).setBody("payload"))

        val result = runBlocking { service.unit() }
        result shouldBe Ok(Unit)
    }

    "expecting and receiving nullable body without annotation returns Err()" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        server.enqueue(MockResponse().setResponseCode(200))

        val result = runBlocking { service.bodyNullable() }
        result shouldBe Err(Processor.emptyError)
    }

    "expecting and receiving nullable body with annotation returns Ok(null)" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        server.enqueue(MockResponse().setResponseCode(200))

        val result = runBlocking { service.bodyNullableAnnotated() }
        result shouldBe Ok(null)
    }

    "ioexception during network operation results in Err()" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        server.enqueue(MockResponse()
            .setBody(Buffer().write(ByteArray(4096)))
            .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY))

        val result = runBlocking { service.body() }
        result shouldBe Err(Processor.exceptionError)
    }


    "network error without error body results in Err()" {
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(Processor))
            .build()
            .create(Service::class.java)

        server.enqueue(MockResponse().setResponseCode(400))

        val result = runBlocking { service.body() }
        result shouldBe Err(Processor.networkError)
    }

    "network error with error body results in Err(errorBody)" {
        val localProcessor = object:ErrorProcessor<String> {
        override fun onEmpty(): String = ""

        override fun onNetworkError(code: Int, errorBody: ResponseBody?): String {
           return errorBody?.string() ?: "Incorrect"
        }

        override fun onException(error: Throwable): String = ""

        override fun onUnknown(detail: String): String = ""
    }

        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ToStringConverterFactory())
            .addCallAdapterFactory(ResultCallAdapterFactory(localProcessor))
            .build()
            .create(Service::class.java)

        val payload = "error body"
        server.enqueue(MockResponse().setResponseCode(400).setBody(payload))

        val result = runBlocking { service.body() }
        result shouldBe Err(payload)
    }
}) {
    interface Service {
        @GET("/")
        suspend fun body(): Result<String, String>

        @GET("/")
        suspend fun bodyNullable(): Result<String?, String>

        @GET("/")
        @NullableBody
        suspend fun bodyNullableAnnotated(): Result<String?, String>

        @GET("/")
        suspend fun unit(): Result<Unit, String>

        @GET("/{a}/{b}/{c}")
        suspend fun params(
            @Path("a") a: String,
            @Path("b") b: String,
            @Path("c") c: String
        ): String
    }

    internal object Processor : ErrorProcessor<String> {
        const val emptyError = "The payload was empty"
        const val networkError = "There was a network error"
        const val exceptionError = "There was an exception"
        const val unknownError = "There was an unknown error"

        override fun onEmpty(): String = emptyError

        override fun onNetworkError(code: Int, errorBody: ResponseBody?): String = networkError

        override fun onException(error: Throwable): String = exceptionError

        override fun onUnknown(detail: String): String = unknownError

    }
}
