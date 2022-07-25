import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import okhttp3.Request
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal open class ResultCall<T, E>(
    internal val delegate: Call<T>,
    internal val preconditions: List<Condition<E>>,
    internal val processor: ErrorProcessor<E>,
) : Call<Result<T, E>> {

    /**
     *
     */
    open fun onEmptySuccess(): Result<T, E> = Err(processor.onEmpty())

    override fun enqueue(callback: Callback<Result<T, E>>) {
        preconditions.forEach { condition ->
            val error = condition.check()
            if (error != null) {
                callback.onResponse(this, Response.success(Err(error)))
                return
            }
        }

        return delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@ResultCall, Response.success(onResponse(response)))
            }

            override fun onFailure(call: Call<T>, error: Throwable) {
                callback.onResponse(this@ResultCall, Response.success(onFailure(error)))
            }
        })
    }

    override fun clone(): Call<Result<T, E>> = ResultCall(delegate.clone(), preconditions, processor)

    override fun execute(): Response<Result<T, E>> {
        preconditions.forEach { condition ->
            val error = condition.check()
            if (error != null) {
                return Response.success(Err(error))
            }
        }

        val result: Result<T, E> = try {
            onResponse(delegate.execute())
        } catch (error: IOException) {
            onFailure(error)
        }
        return Response.success(result)
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel(): Unit = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private fun onResponse(response: Response<T>): Result<T, E> {
        val body = response.body()
        return when {
            response.isSuccessful && body != null -> Ok(body)
            response.isSuccessful -> onEmptySuccess()
            else -> {
                val code = response.code()
                val errorBody = response.errorBody()
                val converted: E = when {
                    errorBody == null -> processor.onUnknown("Received error body is null!")
                    errorBody.contentLength() == 0L -> processor.onUnknown("Received error body is malformed!")
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

/**
 * A special case of [ResultCall] which can succeed without having any payload body
 */
internal class EmptyResultCall<E>(
    delegate: Call<Unit>,
    preconditions: List<Condition<E>>,
    processor: ErrorProcessor<E>,
) : ResultCall<Unit, E>(
    delegate = delegate,
    preconditions = preconditions,
    processor = processor,
) {
    override fun onEmptySuccess(): Result<Unit, E> = Ok(Unit)

    override fun clone(): Call<Result<Unit, E>> = EmptyResultCall(delegate.clone(), preconditions, processor)
}

