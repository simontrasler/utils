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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 *
 * @author Simon Trasler
 */
public class WebCache<V> extends Cache<V> {
    private static final Logger logger = LoggerFactory.getLogger(WebCache.class);

    private final WebClient webClient;
    private final Class clazz;
    private final ObjectMapper objectMapper;
    private final String uriTemplate;
    private final long timeoutMillis;

    // Formatter for the If-Modified-Since header, per
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/If-Modified-Since
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofPattern("EEE, DD MMM yyyy hh:mm:ss 'GMT'")
        .withZone(ZoneOffset.UTC);

    private static final String HEADER_LAST_MODIFIED = "Last-Modified";
    private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

    private WebCache(Builder<V> builder) {
        super(builder);

        WebClientOptions options = new WebClientOptions();

        options.setFollowRedirects(true);
        options.setTcpKeepAlive(true);
        options.setMaxPoolSize(builder.maximumPoolSize);

        this.webClient = WebClient.create(builder.vertx, options);
        this.clazz = builder.clazz;
        this.objectMapper = new ObjectMapper();
        this.uriTemplate = builder.uriTemplate;
        this.timeoutMillis = builder.timeoutMillis;
    }

    @Override
    protected void refresh(CacheValue<V> value) {
        String uri = MessageFormatter.format(uriTemplate, value.getKey()).getMessage();
        logger.debug("Loading data for key:{} from uri:{}", value.getKey(), uri);

        HttpRequest<Buffer> request = webClient.get(uri)
                .timeout(timeoutMillis);

        Instant oldLastModified = value.getLastModified();
        if (oldLastModified != null) {
            request.putHeader(HEADER_IF_MODIFIED_SINCE, FORMATTER.format(oldLastModified));
        }

        request.send()
                .onFailure(e -> {
                    value.notifyUnchanged(true);
                    logger.warn("Failed to get key:{} message:{}", value.getKey(), e.getMessage());
                })
                .onSuccess(handler -> {
                    Instant newLastModified = null;

                    String lastModifiedString = handler.getHeader(HEADER_LAST_MODIFIED);
                    if (lastModifiedString != null) {
                        try {
                            newLastModified = FORMATTER.parse(lastModifiedString, Instant::from);
                        }
                        catch (Throwable t) {
                            // It is not possible to update the contents with the correct
                            // timestamp. Continue with a null timestamp, so a future
                            // refresh will pick up the latest copy of the data.
                            logger.info("Ignoring invalid timestamp for key:{} time:{}", value.getKey(), lastModifiedString);
                        }
                    }

                    switch (handler.statusCode()) {
                        case 200 -> {
                            loadValue(value, handler.body().toString(), newLastModified);
                        }
                        case 204 -> {
                            value.setValue(null, newLastModified);
                        }
                        case 304 -> {
                            // File not changed.
                            value.notifyUnchanged(false);
                        }
                        case 404 -> {
                            // Not found.
                            value.notifyNotFound();
                        }
                        default -> {
                            // We must make sure to clear the 'updating' flag.
                            value.notifyUnchanged(true);
                            logger.warn("Unexpected HTTP status code:{} for key:{}", handler.statusCode(), value.getKey());
                        }
                    }
                });
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
        private String uriTemplate;
        private long timeoutMillis = 60_000;
        private int maximumPoolSize;

        public Builder withVertx(Vertx vertx) {
            this.vertx = vertx;
            return this;
        }

        public Builder withClass(Class clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder withUriTemplate(String uriTemplate) {
            this.uriTemplate = uriTemplate;
            return this;
        }

        public Builder withTimeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public Builder withMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
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

        public WebCache<V> build() {
            return new WebCache<>(this);
        }
    }
}
