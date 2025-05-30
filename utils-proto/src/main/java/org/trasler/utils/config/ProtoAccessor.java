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

import org.trasler.utils.lang.ListPointer;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

/**
 *
 * @author Simon Trasler
 */
public class ProtoAccessor implements TargetingAccessor<String> {
    private final Message message;

    private static final String DELIMITER = ".";

    public ProtoAccessor(Message message) {
        this.message = message;
    }

    @Override
    public String get(String key) {
        // Break the key into a list.
        ListPointer<String> listPointer = new ListPointer<>(key.split(DELIMITER));

        return getField(message, listPointer);
    }

    private String getField(Message message, ListPointer<String> listPointer) {
        Descriptors.Descriptor descriptor = message.getDescriptorForType();
        FieldDescriptor fd = descriptor.findFieldByName(listPointer.peek());

        if (fd != null) {
            Object value = message.getField(fd);

            switch (fd.getType()) {
                case MESSAGE -> { return getField((Message)value, listPointer.next()); }
                default -> { return value.toString(); }
            }
        }

        return null;
    }
}
