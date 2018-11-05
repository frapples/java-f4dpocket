package io.github.frapples.javaf4dpocket.bootstrap;

import io.github.frapples.javaf4dpocket.service.comm.WorkerService;
import io.github.frapples.javaf4dpocket.controller.base.BaseController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */

@Slf4j
public class Verticle extends AbstractVerticle {

    @Inject
    private Set<BaseController> controllers;

    @Inject
    private WorkerService workerService;

    @Override
    public void start(Future<Void> startFuture) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        controllers.forEach(c -> c.init(router));

        server.requestHandler(router::accept).listen(getPort());

        workerService.init(vertx);
    }

    private int getPort() {
        try {
            return Integer.parseInt(System.getProperty("port"));
        } catch (Exception e) {
            return 8080;
        }
    }

}
