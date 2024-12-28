package org.trasler.utils.openrtb;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Extension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author simon
 */
public class ExtensionRegistry {
    private final Map<String, Set<FieldDescriptor>> extensions = new HashMap<>();

    public void add(Extension<?, ?> extension) {
        String name = extension.getDescriptor().getContainingType().getFullName();
        extensions.computeIfAbsent(name, k -> new HashSet<FieldDescriptor>())
                .add(extension.getDescriptor());
    }

    public Set<FieldDescriptor> getAllExtensions(Descriptor descriptor) {
        String name = descriptor.getFullName();
        return extensions.getOrDefault(name, Set.of());
    }
}
