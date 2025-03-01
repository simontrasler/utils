package org.trasler.utils.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import java.util.function.Function;

/**
 *
 * @author simon
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
