import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal open class ResultCall<T, E>(
    private val delegate: Call<T>,
    private val emptyResult: Result<T, E>,
    private val processor: ErrorProcessor<E>,
) : Call<Result<T, E>> {

    override fun enqueue(callback: Callback<Result<T, E>>) {
        return delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@ResultCall, Response.success(onResponse(response)))
            }

            override fun onFailure(call: Call<T>, error: Throwable) {
                callback.onResponse(this@ResultCall, Response.success(onFailure(error)))
            }
        })
    }

    override fun clone(): Call<Result<T, E>> = ResultCall(delegate.clone(), emptyResult, processor)

    override fun execute(): Response<Result<T, E>> {
        throw UnsupportedOperationException("Calling 'execute()' is not supported!")
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel(): Unit = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private fun onResponse(response: Response<T>): Result<T, E> {
        val body: T? = response.body()
        return when {
            response.isSuccessful && body != null -> Ok(body)
            response.isSuccessful -> emptyResult
            else -> {
                val code = response.code()
                val errorBody = response.errorBody()
                val converted: E = when {
                    errorBody == null -> processor.onNetworkError(code, null)
                    errorBody.contentLength() == 0L -> processor.onNetworkError(code, null)
                    else -> try {
                        processor.onNetworkError(code, errorBody)
                    } catch (e: Exception) {
                        processor.onUnknown(e.message ?: "Exception occurred during error processing!")
                    }
                }
                Err(converted)
            }
        }
    }

    private fun onFailure(error: Throwable): Result<T, E> = Err(processor.onException(error))
}