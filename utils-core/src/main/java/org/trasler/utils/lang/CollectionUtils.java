/**
 * MIT License
 *
 * Copyright (c) 2024 Simon Trasler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.trasler.utils.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Simon Trasler
 */
public class CollectionUtils {
    /**
     * Detect if a collection is null or empty.
     *
     * @param collection The collection to check
     * @return True if the collection is null or empty
     */
    public static boolean isEmpty(Collection collection) {
        return (collection != null) ? collection.isEmpty() : true;
    }

    /**
     * Detect if a map is null or empty.
     *
     * @param map The map to check
     * @return True if the map is null or empty
     */
    public static boolean isEmpty(Map map) {
        return (map != null) ? map.isEmpty() : true;
    }

    /**
     * Make a new set that is the union of two others.
     *
     * @param <T> The type of the sets
     * @param first One of the sets to merge
     * @param second One of the sets to merge
     * @return A new set that contains all elements of the inputs
     */
    public static <T> Set<T> union(Set<T> first, Set<T> second) {
        return Stream.concat(first.stream(), second.stream())
                .collect(Collectors.toSet());
    }

    /**
     * Extract a list of strings from a comma-separated list. Any white space
     * surrounding the values is removed.
     *
     * @param csv The comma-separated list
     * @return A list of strings, guaranteed non-null
     */
    public static List<String> csvToStrings(String csv) {
        if (csv != null) {
            List result = new ArrayList<>();

            for (String s : csv.split(",")) {
                result.add(s.trim());
            }

            return result;
        }

        return List.of();
    }

    /**
     * Extract a list of integers from a comma-separated list. Any non-integer
     * values from the list are discarded.
     *
     * @param csv The comma-separated list
     * @return A list of integers, guaranteed non-null
     */
    public static List<Integer> csvToIntegers(String csv) {
        return csvToStrings(csv).stream()
                .map(s -> {
                    try {
                        return Integer.valueOf(s);
                    }
                    catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
