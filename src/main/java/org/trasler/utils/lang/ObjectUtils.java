package org.trasler.utils.lang;

import java.util.function.Consumer;

/**
 *
 * @author simon
 */
public class ObjectUtils {
    /**
     * For a given value of type T, and a function that accepts a single
     * parameter of the same type, if the value is not null then apply the
     * function to it.
     *
     * @param <T> The type
     * @param value The value
     * @param consumer The function to be called with the non-null value
     */
    public static <T> void ifNotNullThen(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }
}
