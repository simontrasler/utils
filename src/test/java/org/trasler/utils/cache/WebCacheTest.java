package org.trasler.utils.cache;

import org.trasler.utils.vertx.cache.CacheValue;
import org.trasler.utils.vertx.cache.WebCache;
import io.vertx.core.Vertx;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author simon
 */
public class WebCacheTest {
    private static final Logger logger = LoggerFactory.getLogger(WebCacheTest.class);

    private WebCache<Example> cache = new WebCache.Builder()
            .withVertx(Vertx.vertx())
            .withClass(Example.class)
            .withUriTemplate("file:///Users/simon/Downloads/Test/{}")
            .withMaximumCacheSize(10)
            .withMaximumPoolSize(10)
            .withTtlSeconds(60)
            .build();

    @Test
    public void testCache() throws InterruptedException {
        CacheValue<Example> result = cache.get("hello");

        for ( ; result.isUpdating(); ) {
            logger.info("Current status:{}", result.getStatus());
            Thread.sleep(1000);
        }

        logger.info("End status:{}", result.getStatus());
    }

    public static class Example {
        String name;

        public String getName() {
            return name;
        }
    }
}
