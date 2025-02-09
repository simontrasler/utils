package org.trasler.utils.vertx.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author simon
 */
public abstract class AbstractMessageCodec<T> implements MessageCodec<T, T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageCodec.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    abstract public Class getMessageClass();

    @Override
    public void encodeToWire(Buffer buffer, T message) {
        try {
            String s = objectMapper.writeValueAsString(message);

            // Prepend the message with its length
            int length = s.getBytes().length;

            // Write data into given buffer
            buffer.appendInt(length);
            buffer.appendString(s);
        }
        catch (JsonProcessingException e) {
            logger.error("Unable to serialize message class:{} reason:{}", name(), e.getMessage());
        }
    }

    @Override
    public T decodeFromWire(int i, Buffer buffer) {
        // Length of JSON
        int length = buffer.getInt(i);
        i += 4;

        String s = buffer.getString(i, i + length);

        T message = null;

        try {
            message = (T)objectMapper.readValue(s, getMessageClass());
        }
        catch (JsonProcessingException e) {
            logger.error("Unable to deserialize message class:{} reason:{}", name(), e.getMessage());
        }

        return message;
    }

    @Override
    public T transform(T message) {
        return message;
    }

    @Override
    public String name() {
        return getMessageClass().getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
