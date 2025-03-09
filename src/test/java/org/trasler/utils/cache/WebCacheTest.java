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
package org.trasler.utils.cache;

import org.trasler.utils.vertx.cache.CacheValue;
import org.trasler.utils.vertx.cache.WebCache;
import io.vertx.core.Vertx;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Simon Trasler
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
