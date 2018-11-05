package io.github.frapples.javaf4dpocket.controller.hello;

import com.google.inject.Singleton;
import io.github.frapples.javaf4dpocket.comm.response.ErrcodeException;
import io.github.frapples.javaf4dpocket.comm.response.ResponseDTO;
import io.github.frapples.javaf4dpocket.service.comm.WorkerService;
import io.github.frapples.javaf4dpocket.comm.tools.RequestTools;
import io.github.frapples.javaf4dpocket.controller.base.BaseController;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
@Slf4j
@Singleton
public class HelloController extends BaseController {

    @Inject
    private WorkerService workerService;


    public Future<?> helloGET(RoutingContext ctx) {
        return RequestTools.decodeAndValid(ctx, HelloDTO.class, ErrcodeException::ofArgumentInvalid)
            .compose((helloDTO -> {
                log.info("{}", helloDTO);
                return Future.succeededFuture(helloDTO);
            }));
    }

    public Future<?> helloPOST(RoutingContext ctx) {
        return RequestTools.decodeAndValid(ctx, HelloDTO.class, ErrcodeException::ofArgumentInvalid)
            .compose((helloDTO -> {
                log.info("{}", helloDTO);
                return Future.succeededFuture(helloDTO);
            }));
    }

    public Future<?> helloBlock(RoutingContext ctx) {
        return workerService.executeBlocking(() -> {
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }).map((v) -> {
            return ResponseDTO.ofSuccess();
        });
    }


    @Override
    public void bind() {
        routeJson(HttpMethod.GET, "/api/hello", this::helloGET);
        routeJson(HttpMethod.POST, "/api/hello", this::helloPOST);
        routeJson(HttpMethod.GET, "/api/hello-block", this::helloBlock);
    }
}
