/**
 * Due to JVM type erasure any information about nullability of type generics in return values
 * will be lost to the [ResultCallAdapter], resulting in an incorrect response in the case
 * of receiving an empty response body for calls allowing for such a result.
 *
 * This annotation signals to the [ResultCallAdapter] that the response can be expected to be
 * nullable.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
public annotation class NullableBody