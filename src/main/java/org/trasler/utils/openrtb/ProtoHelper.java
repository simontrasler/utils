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
package org.trasler.utils.openrtb;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Simon Trasler
 */
public class ProtoHelper {
    private static final Logger logger = LoggerFactory.getLogger(ProtoHelper.class);

    private static final String EXTENSION_NAME = "ext";

    private final ExtensionRegistry registry;

    private ProtoHelper(Builder builder) {
        this.registry = builder.registry;
    }

    /**
     * Convert a JSON message to the provided Protocol Buffers schema.
     *
     * @param <T> The schema
     * @param builder Builder for the schema
     * @param json The JSON message
     * @return An equivalent Protocol Buffers message in the given schema
     */
    public <T extends Message> T fromJson(T.Builder builder, JsonObject json) {
        try {
            messageFromJson(builder, json);
            return (T)builder.build();
        }
        catch (Exception e) {
            logger.error("Type:{} message:{}", e.getClass(), e.getMessage());
        }

        return null;
    }

    private boolean messageFromJson(Message.Builder builder, JsonObject json) {
        boolean updated = false;

        Descriptors.Descriptor descriptor = builder.getDescriptorForType();

        for (String key : json.fieldNames()) {
            if (EXTENSION_NAME.equals(key)) {
                if (registry != null) {
                    // Handle extensions.
                    JsonObject ext = json.getJsonObject(key);

                    // Offer all fields to each applicable extension object.
                    for (Descriptors.FieldDescriptor fd : registry.getAllExtensions(descriptor)) {
                        Object value = messageFromJson(builder, fd, ext);

                        if (value != null) {
                            builder.setField(fd, value);
                            updated = true;
                        }
                    }
                } else {
                    logger.warn("No registry to parse extensions in message:{}", descriptor.getFullName());
                }
            } else {
                // Get the target field type.
                Descriptors.FieldDescriptor fd = descriptor.findFieldByName(key);

                if (fd != null) {
                    // Populate the target object, converting the type as necessary.
                    if (fd.isRepeated()) {
                        JsonArray array = json.getJsonArray(key);

                        for (Object value : array) {
                            value = valueFromJson(builder, fd, value);

                            if (value != null) {
                                builder.addRepeatedField(fd, value);
                                updated = true;
                            }
                        }
                    } else {
                        Object value = json.getValue(key);
                        value = valueFromJson(builder, fd, value);

                        if (value != null) {
                            builder.setField(fd, value);
                            updated = true;
                        }
                    }
                }
            }
        }

        return updated;
    }

    /**
     * Coerce a JSON field value to the Protocol Buffers expected type. This
     * is deliberately lenient, to avoid discarding messages for trifling
     * reasons when the intent is clear, e.g., an integer is expected, yet the
     * JSON is an integer value written as a string.
     *
     * @param builder The Protocol Buffers builder for the current object
     * @param fd The field descriptor in that object
     * @param value The JSON value
     * @return The coerced value
     */
    private Object valueFromJson(Message.Builder builder, Descriptors.FieldDescriptor fd, Object value) {
        switch (fd.getType()) {
            case Type.INT32 -> { value = integerFromJson(value); }
            case Type.INT64 -> { value = longFromJson(value); }
            case Type.FLOAT -> { value = floatFromJson(value); }
            case Type.DOUBLE -> { value = doubleFromJson(value); }
            case Type.BOOL -> { value = booleanFromJson(value); }
            case Type.STRING -> { value = stringFromJson(value); }
            case Type.MESSAGE -> { value = messageFromJson(builder, fd, value); }
        }

        return value;
    }

    private Object integerFromJson(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Integer.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Integer.class);
                }
            }
            case Boolean b -> {
                return b ? 1 : 0;
            }
            default -> {
                // No-op.
            }
        }

        return value;
    }

    private Object longFromJson(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Long.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Long.class);
                }
            }
            case Boolean b -> {
                return b ? 1L : 0L;
            }
            default -> {
                // No-op.
            }
        }

        return value;
    }

    private Object booleanFromJson(Object value) {
        switch (value) {
            case String s -> {
                if ("1".equals(s) || "true".equals(s) || "TRUE".equals(s)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            case Long l -> {
                return (l != 0);
            }
            case Integer i -> {
                return (i != 0);
            }
            case Double d -> {
                return (d != 0.0);
            }
            case Float f -> {
                return (f != 0.0);
            }
            default -> {
                // No-op.
            }
        }

        return value;
    }

    private Object floatFromJson(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Float.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Float.class);
                }
            }
            default -> {
                // No-op.
            }
        }

        return value;
    }

    private Object doubleFromJson(Object value) {
        switch (value) {
            case String s -> {
                try {
                    return Double.valueOf(s);
                }
                catch (NumberFormatException e) {
                    // Fall through.
                    logger.warn("Could not convert from:{} to:{}", value.getClass(), Double.class);
                }
            }
            default -> {
                // No-op.
            }
        }

        return value;
    }

    private Object stringFromJson(Object value) {
        switch (value) {
            case String s -> {
                return s;
            }
            case Boolean b -> {
                return b ? "1" : "0";
            }
            default -> {
                return value.toString();
            }
        }
    }

    private Object messageFromJson(Message.Builder builder, Descriptors.FieldDescriptor fd, Object value) {
        if (value instanceof JsonObject jsonObject) {
            Message.Builder childBuilder = builder.newBuilderForField(fd);

            if (messageFromJson(childBuilder, jsonObject)) {
                return childBuilder.build();
            }

            return null;
        }

        logger.warn("Could not convert field:{} from:{} to:{}", fd.getName(), value.getClass(), Message.class);
        throw new IllegalArgumentException("Field:" + fd.getFullName());
    }

    /**
     * Convert a Protocol Buffers message to JSON.
     *
     * @param message The Protocol Buffers message
     * @return An equivalent JSON object
     */
    public JsonObject toJson(Message message) {
        JsonObject json = new JsonObject();

        messageToJson(json, message);
        return json;
    }

    private boolean messageToJson(JsonObject json, Message message) {
        boolean updated = false;

        Descriptors.Descriptor descriptor = message.getDescriptorForType();

        for (Descriptors.FieldDescriptor fd : descriptor.getFields()) {
            if (fd.isRepeated() ? message.getRepeatedFieldCount(fd) == 0 : !message.hasField(fd)) {
                // Skip fields not explicitly set.
                continue;
            }

            if (fd.isRepeated()) {
                JsonArray array = new JsonArray();

                for (int i = 0; i < message.getRepeatedFieldCount(fd); i++) {
                    Object value = valueToJson(fd, message.getRepeatedField(fd, i));
                    array.add(value);
                }

                // At this point we are guaranteed at least one value, so always
                // emit the array.
                json.put(fd.getName(), array);
                updated = true;
            } else {
                Object value = valueToJson(fd, message.getField(fd));

                if (value != null) {
                    json.put(fd.getName(), value);
                    updated = true;
                }
            }
        }

        if (registry != null) {
            for (Descriptors.FieldDescriptor fd : registry.getAllExtensions(descriptor)) {
                if (fd.isRepeated() || !message.hasField(fd)) {
                    // OpenRTB does not support repeated extensions so skip them,
                    // in addition to fields not explicitly set.
                    continue;
                }

                Object value = valueToJson(fd, message.getField(fd));

                if (value != null && value instanceof JsonObject childJson) {
                    // Lazily instantiate the extension object.
                    JsonObject ext = json.getJsonObject(EXTENSION_NAME);

                    if (ext == null) {
                        ext = new JsonObject();
                        json.put(EXTENSION_NAME, ext);
                    }

                    // Combine the fields for all the extensions.
                    ext.mergeIn(childJson);
                    updated = true;
                }
            }
        }

        return updated;
    }

    private Object valueToJson(Descriptors.FieldDescriptor fd, Object value) {
        switch (fd.getType()) {
            case Type.BOOL -> { value = Boolean.TRUE.equals(value) ? 1 : 0; }
            case Type.MESSAGE -> { value = messageToJson(fd, value); }
        }

        // For all other types, the automatic conversion is sufficient.
        return value;
    }

    private Object messageToJson(Descriptors.FieldDescriptor fd, Object value) {
        if (value instanceof Message message) {
            JsonObject json = new JsonObject();

            if (messageToJson(json, message)) {
                return json;
            }

            return null;
        }

        logger.warn("Could not convert field:{} from:{} to:{}", fd.getName(), value.getClass(), Message.class);
        throw new IllegalArgumentException("Field:" + fd.getFullName());
    }

    public static class Builder {
        private ExtensionRegistry registry;

        public Builder withExtensionRegistry(ExtensionRegistry registry) {
            this.registry = registry;
            return this;
        }

        public ProtoHelper build() {
            return new ProtoHelper(this);
        }
    }
}
