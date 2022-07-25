import retrofit2.Call
import retrofit2.CallAdapter
import com.github.michaelbull.result.Result
import java.lang.reflect.Type

internal class ResultCallAdapter<T, E>(
    private val successType: Type,
    private val processor: ErrorProcessor<E>,
    private val preconditions: List<Condition<E>>,
) : CallAdapter<T, Call<Result<T, E>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<Result<T, E>> = ResultCall(call, preconditions, processor)
}

internal class EmptyCallAdapter<E>(
    private val processor: ErrorProcessor<E>,
    private val preconditions: List<Condition<E>>,
) : CallAdapter<Unit, Call<Result<Unit, E>>> {

    override fun responseType(): Type = Unit::class.java

    override fun adapt(call: Call<Unit>): Call<Result<Unit, E>> = EmptyResultCall(call, preconditions, processor)
}