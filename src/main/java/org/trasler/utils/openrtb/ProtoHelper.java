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
 * @author simon
 */
public class ProtoHelper {
    private static final Logger logger = LoggerFactory.getLogger(ProtoHelper.class);

    private static final String EXTENSION_NAME = "ext";

    private final ExtensionRegistry registry;

    private ProtoHelper(Builder builder) {
        this.registry = builder.registry;
    }

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
                    logger.warn("Extension ignored, no registry");
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
                    Object value = valueToJson(fd, message.getField(fd));
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
