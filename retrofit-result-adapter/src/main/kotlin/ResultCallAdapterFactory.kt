import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import com.github.michaelbull.result.Result


public class ResultCallAdapterFactory<E>(
    private val processor: ErrorProcessor<E>
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        return when (getRawType(returnType)) {
            Call::class.java -> {
                val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                val rawType = getRawType(callType)
                if (rawType != Result::class.java) {
                    return null
                }

                val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                val emptyResult = when {
                    resultType == Unit::class.java -> Ok(Unit)
                    annotations.contains(NullableBody()) -> Ok(null)
                    else -> Err(processor.onEmpty())
                }
                ResultCallAdapter(resultType, emptyResult, processor)
            }
            else -> null
        }
    }
}