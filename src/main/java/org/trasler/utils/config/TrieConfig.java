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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Trie-style configuration. This class supports complex configuration scenarios
 * by taking in a request context and mapping it onto a trie of configured
 * options. If it walks to a node where this is no match, it backtracks to find
 * the best match.
 *
 * @author Simon Trasler
 * @param <T> The type of the configurations
 */
@JsonDeserialize(builder = TrieConfig.Builder.class)
public class TrieConfig<T> {
    private final List<String> keys;
    private final TrieConfigNode<T> values;

    private static final String KEYS = "keys";
    private static final String COMMENT = "#";
    private static final String TAB_DELIMITER = "\\t";
    private static final String LIST_DELIMITER = ",";
    private static final String VALUE_DELIMITER = "/";
    private static final String VALUE_PREFIX = "value" + VALUE_DELIMITER;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TrieConfig(Builder<T> builder) {
        this.keys = builder.keys;
        this.values = builder.values;
    }

    public T get(TargetingAccessor<String> accessor) {
        return values.get(new ListPointer<>(keys), accessor);
    }

    public static <T> TrieConfig<T> from(Path path, Class<T> clazz) throws IOException {
        List<String> lines = Files.readAllLines(path);

        TrieConfig.Builder<T> builder = new TrieConfig.Builder<>();

        TrieConfigNode.Builder<T> valueBuilder = new TrieConfigNode.Builder<>();
        builder.withValues(valueBuilder);

        for (String line : lines) {
            line = line.trim();

            if (line.length() == 0) {
                // Skip blank lines.
                continue;
            } else if (line.startsWith(COMMENT)) {
                // Skip comments.
                continue;
            }

            String[] split = line.split(TAB_DELIMITER, 2);

            if (KEYS.equals(split[0])) {
                // Ingest the list of targeting keys.
                builder.withKeys(Arrays.asList(split[1].split(LIST_DELIMITER)));
            } else if (split[0].startsWith(VALUE_PREFIX)) {
                // Ingest the list of targeting values, to be used to look up a
                // specific result.
                split[0] = split[0].substring(VALUE_PREFIX.length());

                // Get the list of targeting values.
                String[] targeting = split[0].split(VALUE_DELIMITER);

                // Get the result.
                T value = objectMapper.readValue(split[1], clazz);

                // Iterate over the list of targeting values, creating a nested
                // map at each step. At the end of the list, record the result.
                populateValueBuilder(valueBuilder, new ListPointer<>(targeting), value);
            }
        }

        return builder.build();
    }

    private static <T> void populateValueBuilder(TrieConfigNode.Builder<T> valueBuilder, ListPointer<String> listPointer, T value) {
        if (listPointer.hasNext()) {
            TrieConfigNode.Builder<T> nextValueBuilder = valueBuilder.getOrCreateMap().computeIfAbsent(listPointer.peek(), k -> {
                return new TrieConfigNode.Builder<>();
            });

            populateValueBuilder(nextValueBuilder, listPointer.next(), value);
        } else {
            valueBuilder.withValue(value);
        }
    }

    public static class Builder<T> {
        private List<String> keys;
        private TrieConfigNode<T> values;
        private TrieConfigNode.Builder<T> valuesBuilder;

        public Builder withKeys(List<String> keys) {
            this.keys = keys;
            return this;
        }

        public Builder withValues(TrieConfigNode.Builder<T> valuesBuilder) {
            this.valuesBuilder = valuesBuilder;
            return this;
        }

        public TrieConfig<T> build() {
            if (valuesBuilder != null) {
                values = valuesBuilder.build();
            }
            return new TrieConfig<>(this);
        }
    }
}
