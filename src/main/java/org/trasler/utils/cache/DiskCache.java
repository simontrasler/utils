package org.trasler.utils.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 *
 * @author simon
 */
public class DiskCache<V> extends Cache<V> {
    private static final Logger logger = LoggerFactory.getLogger(DiskCache.class);
    
    private final FileSystem fs;
    private final String fileTemplate;
    private final Class clazz;
    private final ObjectMapper objectMapper;
    
    private DiskCache(DiskCache.Builder<V> builder) {
        super(builder);
        
        this.fs = builder.vertx.fileSystem();
        
        this.fileTemplate = builder.fileTemplate;
        this.clazz = builder.clazz;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void refresh(CacheValue<V> value) {
        // Start by fetching the properties of the file. Then, if it is found,
        // compare the last-modified time of the file to that of the data we
        // have loaded already (if any), and act accordingly.
        String filename = MessageFormatter.format(fileTemplate, value.getKey()).getMessage();
        logger.debug("Loading data for key:{} from file:{}", value.getKey(), filename);
        
        fs.props(filename)
                .onFailure(e -> {
                    value.notifyNotFound();
                    logger.warn("Failed to find key:{} message:{}", value.getKey(), e.getMessage());
                })
                .compose((fp) -> handleUpdate(filename, fp, value));
    }
    
    private Future<Buffer> handleUpdate(String filename, FileProps fp, CacheValue<V> value) {
        Instant lastModified = value.getLastModified();
        Instant newLastModified = Instant.ofEpochSecond(fp.lastModifiedTime());

        if (lastModified == null || lastModified.isBefore(newLastModified)) {
            // Load the update.
            return fs.readFile(filename)
                    .onSuccess(buffer -> {
                        if (buffer.length() == 0) {
                            value.setValue(null, newLastModified);
                        } else {
                            loadValue(value, buffer.toString(), newLastModified);
                        }
                    })
                    .onFailure(e -> {
                        value.notifyUnchanged(true);
                        logger.warn("Failed to read data for key:{} message:{}", value.getKey(), e.getMessage());
                    });
        } else {
            // The data is unchanged.
            value.notifyUnchanged(false);
            return Future.succeededFuture();
        }
    }
    
    private void loadValue(CacheValue<V> cacheValue, String s, Instant newLastModified) {
        try {
            V value = (V) objectMapper.readValue(s, clazz);
            cacheValue.setValue(value, newLastModified);
        }
        catch (JsonProcessingException e) {
            logger.warn("Failed to map JSON for key:{} message:{}", cacheValue.getKey(), e.getMessage());
            cacheValue.notifyUnchanged(true);
        }
    }
    
    public static class Builder<V> extends Cache.Builder<String, CacheValue<V>> {
        private Vertx vertx;
        private Class clazz;
        private String fileTemplate;
        
        public Builder withVertx(Vertx vertx) {
            this.vertx = vertx;
            return this;
        }
        
        public Builder withClass(Class clazz) {
            this.clazz = clazz;
            return this;
        }
        
        public Builder withFileTemplate(String fileTemplate) {
            this.fileTemplate = fileTemplate;
            return this;
        }
        
        @Override
        public Builder withMaximumCacheSize(int maximumCacheSize) {
            super.withMaximumCacheSize(maximumCacheSize);
            return this;
        }

        @Override
        public Builder withTtlSeconds(int ttl) {
            super.withTtlSeconds(ttl);
            return this;
        }
        
        public DiskCache<V> build() {
            return new DiskCache<>(this);
        }
    }
}