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

import java.util.List;
import java.util.Map;

/**
 *
 * @author Simon Trasler
 */
public class CollectionUtils {
    /**
     * Detect if a list is non-null or empty.
     *
     * @param list The list to check
     * @return True if the list is non-null or empty
     */
    public static boolean isEmpty(List list) {
        return (list != null) ? list.isEmpty() : true;
    }

    /**
     * Detect if a map is non-null or empty.
     *
     * @param map The list to check
     * @return True if the map is non-null or empty
     */
    public static boolean isEmpty(Map map) {
        return (map != null) ? map.isEmpty() : true;
    }
}
