public interface Condition<out E> {

    public fun check(): E?
}