package org.trasler.utils.cache;

import io.vertx.core.Vertx;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author simon
 */
public class DiskCacheTest {
    private static final Logger logger = LoggerFactory.getLogger(DiskCacheTest.class);

    private DiskCache<DiskCacheTest.Example> cache = new DiskCache.Builder()
            .withVertx(Vertx.vertx())
            .withClass(DiskCacheTest.Example.class)
            .withFileTemplate("/Users/simon/Downloads/Test/{}")
            .withMaximumCacheSize(10)
            .withTtlSeconds(60)
            .build();

    @Test
    public void testCache() throws InterruptedException {
        CacheValue<DiskCacheTest.Example> result = cache.get("hello");

        for ( ; result.isUpdating(); ) {
            logger.info("Current status:{}", result.getStatus());
            Thread.sleep(1000);
        }

        logger.info("End status:{}", result.getStatus());
    }

    public static class Example {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
