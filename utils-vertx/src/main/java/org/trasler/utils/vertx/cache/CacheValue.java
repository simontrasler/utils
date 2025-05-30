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
package org.trasler.utils.vertx.cache;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Simon Trasler
 * @param <V> The type of the value
 */
public class CacheValue<V> {
    private static final Logger logger = LoggerFactory.getLogger(CacheValue.class);

    private final String key;
    private V value;
    private Instant lastModified;
    private Status status;
    private boolean updating;
    private boolean stale;

    private CacheValue(String key, V value, Instant lastModified, Status status, boolean updating, boolean stale) {
        this.key = key;
        this.value = value;
        this.lastModified = lastModified;
        this.status = status;
        this.updating = updating;
        this.stale = stale;
    }

    public static CacheValue newInstance(String key) {
        return new CacheValue(key, null, null, Status.UNDEFINED, true, false);
    }

    public static CacheValue from(CacheValue other) {
        CacheValue newValue;

        synchronized (other) {
            newValue = new CacheValue(other.key, other.value, other.lastModified, other.status, true, other.stale);
        }

        return newValue;
    }

    public String getKey() {
        return key;
    }

    public synchronized void setValue(V value, Instant lastModified) {
        this.value = value;
        this.lastModified = lastModified;
        this.updating = false;
        this.stale = false;

        if (this.value != null) {
            this.status = Status.VALID;
            logger.debug("Updated key:{} to valid entry", this.key);
        } else {
            this.status = Status.EMPTY;
            logger.debug("Updated key:{} to empty entry", this.key);
        }
    }

    public synchronized V getValue() {
        return value;
    }

    public synchronized Instant getLastModified() {
        return lastModified;
    }

    public synchronized void notifyUnchanged(boolean stale) {
        this.updating = false;
        this.stale = stale;

        logger.debug("Updated key:{} as unchanged", this.key);
    }

    public synchronized void notifyNotFound() {
        this.updating = false;
        this.stale = false;
        this.status = Status.NOT_FOUND;

        logger.debug("Updated key:{} as not found", this.key);
    }

    public synchronized Status getStatus() {
        return status;
    }

    public synchronized boolean isUpdating() {
        return updating;
    }

    public synchronized boolean isStale() {
        return stale;
    }

    public synchronized boolean hasValue() {
        return Status.VALID.equals(status) || Status.EMPTY.equals(status);
    }

    @Override
    public synchronized String toString() {
        return "CacheValue" +
                " status:" + status +
                " stale:" + stale +
                " updating:" + updating +
                " lastModified:" + lastModified +
                " value:" + value;
    }

    private enum Status {
        // The backing value has not yet been obtained.
        UNDEFINED,

        // There is no backing value for this key.
        NOT_FOUND,

        // This key was found and its backing value is null.
        EMPTY,

        // The backing value for this key is non-null.
        VALID;
    }
}
