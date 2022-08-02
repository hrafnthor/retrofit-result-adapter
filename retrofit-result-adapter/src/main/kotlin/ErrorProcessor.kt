import okhttp3.ResponseBody

public interface ErrorProcessor<E> {

    public fun onEmpty(): E

    public fun onNetworkError(code: Int, errorBody: ResponseBody?): E

    public fun onException(error: Throwable): E

    public fun onUnknown(detail: String): E
}