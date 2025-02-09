package org.trasler.utils.vertx.cache;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author simon
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
