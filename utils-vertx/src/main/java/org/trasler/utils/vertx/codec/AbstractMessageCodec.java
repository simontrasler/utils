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
package org.trasler.utils.vertx.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Simon Trasler
 */
public abstract class AbstractMessageCodec<T> implements MessageCodec<T, T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageCodec.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    abstract public Class<T> getMessageClass();

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
            message = objectMapper.readValue(s, getMessageClass());
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
