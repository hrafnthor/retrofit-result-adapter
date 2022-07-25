import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


public class ResultCallAdapterFactory<E>(
    private val processor: ErrorProcessor<E>,
    private val preconditions: List<Condition<E>> = emptyList()
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        return when (val type = getRawType(returnType)) {
            Call::class.java -> {
                val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                val rawType = getRawType(callType)
                if (rawType != Result::class.java) {
                    return null
                }

                val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                if (resultType == Unit::class.java) {
                    EmptyCallAdapter(processor, preconditions)
                } else {
                    ResultCallAdapter(resultType, processor, preconditions)
                }
            }
            else -> null
        }
    }
}
