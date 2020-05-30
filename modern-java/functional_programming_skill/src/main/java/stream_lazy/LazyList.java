package stream_lazy;

public interface LazyList<T> {
    T head();

    LazyList<T> tail();

    default boolean isEmpty(){
        return true;
    }
}
