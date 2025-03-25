package inf.elte.hu.gameengine_javafx.Misc;

/**
 * A generic record that represents a pair of values of types {@code T} and {@code U}.
 * <p>
 * This record class provides a simple container for two objects, often used to group related data together.
 * The first value is of type {@code T}, and the second value is of type {@code U}.
 * </p>
 *
 * @param <T> the type of the first element of the tuple
 * @param <U> the type of the second element of the tuple
 */
public record Tuple<T, U>(T first, U second) {
}
