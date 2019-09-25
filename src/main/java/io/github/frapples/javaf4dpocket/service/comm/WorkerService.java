package io.github.frapples.javaf4dpocket.service.comm;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import java.util.function.Supplier;
import javax.inject.Singleton;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-5
 */

@Singleton
public class WorkerService {

    private WorkerExecutor workerExecutor;

    public void init(Vertx vertx) {
        if (workerExecutor != null) {
            workerExecutor.close();
        }
        this.workerExecutor = vertx.createSharedWorkerExecutor("common-worker-pool");
    }

    public <T> void executeBlocking(Handler<Promise<T>> blockingCodeHandler,
        boolean ordered, Handler<AsyncResult<T>> resultHandler) {
        workerExecutor.executeBlocking(blockingCodeHandler, ordered, resultHandler);
    }

    public <T> void executeBlocking(Handler<Promise<T>> blockingCodeHandler, Handler<AsyncResult<T>> resultHandler) {
        workerExecutor.executeBlocking(blockingCodeHandler, resultHandler);
    }

    public <T> Future<T> executeBlocking(Supplier<T> blockingCodeHandler) {
        Promise<T> promise = Promise.promise();
        workerExecutor.executeBlocking((f) -> f.complete(blockingCodeHandler.get()), promise);
        return promise.future();
    }
}
