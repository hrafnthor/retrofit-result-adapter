import retrofit2.Call
import retrofit2.CallAdapter
import com.github.michaelbull.result.Result
import java.lang.reflect.Type

internal class ResultCallAdapter<T, E>(
    private val successType: Type,
    private val emptyResult: Result<T, E>,
    private val processor: ErrorProcessor<E>,
) : CallAdapter<T, Call<Result<T, E>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<Result<T, E>> = ResultCall(call, emptyResult, processor)
}