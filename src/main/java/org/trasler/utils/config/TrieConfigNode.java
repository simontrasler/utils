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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Simon Trasler
 * @param <T>
 */
@JsonDeserialize(builder = TrieConfigNode.Builder.class)
public class TrieConfigNode<T> {
    private final Map<String, TrieConfigNode<T>> map;
    private final T value;

    public static final String WILDCARD = "*";

    private TrieConfigNode(Builder<T> builder) {
        this.map = builder.map;
        this.value = builder.value;
    }

    public T get(ListPointer<String> keys, TargetingAccessor<String> accessor) {
        if (map != null) {
            // Keep iterating to find the best match.
            String targetingValue = accessor.get(keys.peek());

            TrieConfigNode<T> node = map.get(targetingValue);

            if (node != null) {
                T result = node.get(keys.next(), accessor);
                keys.back();

                if (result != null) {
                    return result;
                }
            }

            // Backtrack from the precise match to the next-best option.
            node = map.get(WILDCARD);

            if (node != null) {
                T result = node.get(keys.next(), accessor);
                keys.back();

                if (result != null) {
                    return result;
                }
            }
        }

        // All else failed, return what we have.
        return value;
    }

    public static class Builder<T> {
        @JsonProperty("map")
        private Map<String, TrieConfigNode<T>> map;

        @JsonProperty("value")
        private T value;

        @JsonIgnore
        private Map<String, TrieConfigNode.Builder<T>> builderMap;

        public Builder withMap(Map<String, TrieConfigNode<T>> map) {
            this.map = map;
            return this;
        }

        public Builder withValue(T value) {
            this.value = value;
            return this;
        }

        @JsonIgnore
        public Map<String, TrieConfigNode.Builder<T>> getOrCreateMap() {
            if (builderMap == null) {
                builderMap = new HashMap<>();
            }
            return builderMap;
        }

        public TrieConfigNode<T> build() {
            if (builderMap != null) {
                if (map == null) {
                    map = new HashMap<>();
                }

                builderMap.forEach((k, v) -> {
                    map.put(k, v.build());
                });
            }

            return new TrieConfigNode<>(this);
        }
    }
}
