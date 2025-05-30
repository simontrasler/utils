/**
 * MIT License
 *
 * Copyright (c) 2025 Simon Trasler
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
package org.trasler.utils.proto;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Extension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Simon Trasler
 */
public class ExtensionRegistry {
    private final Map<String, Entry> entries = new HashMap<>();

    // Default entry with immutable collections.
    private static final Entry EMPTY_ENTRY = new Entry();

    public void add(Extension<?, ?> extension) {
        String name = extension.getDescriptor().getContainingType().getFullName();
        entries.computeIfAbsent(name, k -> new Entry())
                .add(extension);
    }

    public Set<FieldDescriptor> getAllExtensions(Descriptor descriptor) {
        String name = descriptor.getFullName();
        return entries.getOrDefault(name, EMPTY_ENTRY).fds;
    }

    public com.google.protobuf.ExtensionRegistry toGoogleRegistry() {
        com.google.protobuf.ExtensionRegistry googleRegistry = com.google.protobuf.ExtensionRegistry.newInstance();

        // Copy across all the extensions.
        entries.forEach((k, v) -> v.extensions.forEach(x -> googleRegistry.add(x)));
        return googleRegistry;
    }

    private static class Entry {
        private List<Extension<?, ?>> extensions = List.of();
        private Set<FieldDescriptor> fds = Set.of();

        /**
         * Update the unmodifiable collections for this entry. This operation is
         * slow to write, so it can be fast and safe to read.
         *
         * @param extension The extension to add
         */
        public void add(Extension<?, ?> extension) {
            // Extensions.
            List<Extension<?, ?>> localExtensions = new ArrayList<>(extensions);
            localExtensions.add(extension);
            extensions = Collections.unmodifiableList(localExtensions);

            // Field descriptors.
            Set<FieldDescriptor> localFds = new HashSet<>(fds);
            localFds.add(extension.getDescriptor());
            fds = Collections.unmodifiableSet(localFds);
        }
    }
}
