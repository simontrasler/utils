/**
 * The MIT License
 *
 * Copyright 2025 Simon Trasler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.trasler.utils.config;

import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Simon Trasler
 */
public class TrieConfigNodeTest {
    private static final MapAccessor<String> US_REQUEST = new MapAccessor<>(Map.of("country", "us"));
    private static final MapAccessor<String> CA_REQUEST = new MapAccessor<>(Map.of("country", "ca"));
    private static final MapAccessor<String> MX_REQUEST = new MapAccessor<>(Map.of("country", "mx"));

    @Test
    public void testWildcard() {
        TrieConfigNode<Integer> trieConfigNode = new TrieConfigNode.Builder<>()
                .withMap(Map.of(
                        "us", new TrieConfigNode.Builder<>()
                                .withValue(1)
                                .build(),
                        "ca", new TrieConfigNode.Builder<>()
                                .withValue(2)
                                .build(),
                        "*", new TrieConfigNode.Builder<>()
                                .withValue(3)
                                .build()))
                .withValue(4)
                .build();

        ListPointer<String> listPointer = new ListPointer(List.of("country"));

        assertEquals(Integer.valueOf(1), trieConfigNode.get(listPointer, US_REQUEST));
        assertEquals(Integer.valueOf(2), trieConfigNode.get(listPointer, CA_REQUEST));
        assertEquals(Integer.valueOf(3), trieConfigNode.get(listPointer, MX_REQUEST));
    }

    @Test
    public void testFallback() {
        TrieConfigNode<Integer> trieConfigNode = new TrieConfigNode.Builder<>()
                .withMap(Map.of(
                        "us", new TrieConfigNode.Builder<>()
                                .withValue(1)
                                .build(),
                        "ca", new TrieConfigNode.Builder<>()
                                .withValue(2)
                                .build()))
                .withValue(4)
                .build();

        ListPointer<String> listPointer = new ListPointer(List.of("country"));

        assertEquals(Integer.valueOf(1), trieConfigNode.get(listPointer, US_REQUEST));
        assertEquals(Integer.valueOf(2), trieConfigNode.get(listPointer, CA_REQUEST));
        assertEquals(Integer.valueOf(4), trieConfigNode.get(listPointer, MX_REQUEST));
    }
}
