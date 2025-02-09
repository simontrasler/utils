package org.trasler.utils.vertx.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

/**
 * Cache for ad-serving purposes. In this case, it is more valuable to retain
 * stale data and refresh it in the background, than to evict it proactively.
 * Eviction can still happen if the cache exceeds a certain size.
 *
 * @author simon
 * @param <V> The type of the value
 */
public abstract class Cache<V> {
    private final LoadingCache<String, CacheValue<V>> cache;

    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MINIMUM_DURATION = MILLIS_PER_SECOND * 60 * 5;

    protected Cache(Builder builder) {
        cache = Caffeine.newBuilder()
                .maximumSize(builder.maximumCacheSize)
                .refreshAfterWrite(builder.ttl, TimeUnit.SECONDS)
                .expireAfter(new Expiry<String, CacheValue<V>>() {
                    @Override
                    public long expireAfterCreate(String key, CacheValue<V> cv, long currentTime) {
                        return Long.MAX_VALUE;
                    }

                    @Override
                    public long expireAfterUpdate(String key, CacheValue<V> cv, long currentTime, long currentDuration) {
                        return Long.MAX_VALUE;
                    }

                    @Override
                    public long expireAfterRead(String key, CacheValue<V> cv, long currentTime, long currentDuration) {
                        if (cv.hasValue() || cv.isUpdating()) {
                            return Long.MAX_VALUE;
                        }

                        // Proven not defined, retain for a short period of time
                        // to reduce the amount of re-polling for invalid keys.
                        return MINIMUM_DURATION;
                    }
                })
                .build(new CacheLoader<>() {
                    /**
                     * Create new entry for this key and initiate the load in
                     * the background.
                     */
                    @Override
                    public CacheValue<V> load(String key) {
                        return valueForKey(key, null);
                    }

                    /**
                     * Create new entry for this key, retaining the old value
                     * until (if) it is replaced by the background thread.
                     */
                    @Override
                    public CacheValue<V> reload(String key, CacheValue<V> oldValue) {
                        return valueForKey(key, oldValue);
                    }
                });
    }

    public CacheValue<V> get(String key) {
        return cache.get(key);
    }

    protected CacheValue<V> valueForKey(String key, CacheValue<V> oldCacheValue) {
        CacheValue newCacheValue;

        if (oldCacheValue != null) {
            // If still updating, throw an exception so it is not replaced.
            if (oldCacheValue.isUpdating()) {
                throw new IllegalStateException("Value already being updated for key:" + key);
            }

            // Otherwise, we are obliged to create a new entry, and copy the
            // old state across.
            newCacheValue = CacheValue.from(oldCacheValue);
        } else {
            // Cache miss.
            newCacheValue = CacheValue.newInstance(key);
        }

        // Initiate checking for new data.
        refresh(newCacheValue);

        return newCacheValue;
    }

    abstract protected void refresh(CacheValue<V> value);

    public static class Builder<K, V> {
        private int maximumCacheSize = 1;
        private int ttl = 5;

        public Builder withMaximumCacheSize(int maximumCacheSize) {
            this.maximumCacheSize = maximumCacheSize;
            return this;
        }

        public Builder withTtlSeconds(int ttl) {
            this.ttl = ttl;
            return this;
        }
    }
}
