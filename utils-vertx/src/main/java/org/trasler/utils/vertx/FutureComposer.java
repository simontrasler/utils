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
package org.trasler.utils.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import java.util.function.Function;

/**
 *
 * @author Simon Trasler
 */
public class FutureComposer<T> {
    private Promise promise;
    private Future<T> future;

    public Future<T> compose(Function<T, Future<T>> mapper) {
        if (future != null) {
            // There is a chain in progress, add to it.
            future = future.compose(mapper);
        } else {
            // Start a new chain.
            promise = Promise.promise();
            future = promise.future().compose(mapper);
        }

        return future;
    }

    public Future<T> complete(Handler<AsyncResult<T>> handler) {
        future = future.andThen(handler);
        return future;
    }

    public void run(T initialValue) {
        if (promise != null) {
            promise.complete(initialValue);
        }
    }
}
