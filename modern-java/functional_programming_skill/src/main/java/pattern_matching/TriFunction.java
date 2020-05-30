package pattern_matching;

public interface TriFunction<S, T, U, R> {
    R apply(S s, T t, U u);
}
