package io.github.frapples.javaf4dpocket.controller.base;

import io.github.frapples.javaf4dpocket.comm.response.ErrcodeException;
import io.github.frapples.javaf4dpocket.comm.response.ResponseDTO;
import io.github.frapples.javaf4dpocket.comm.tools.ResponseTools;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public abstract class BaseController {

    @Getter(AccessLevel.PROTECTED)
    private Router router;

    public void init(Router router) {
        this.router = router;
        this.bind();
    }

    public abstract void bind();

    public Handler<RoutingContext> wrap(Function<RoutingContext, Future<?>> hander) {
        return (RoutingContext routingContext) -> {
            hander.apply(routingContext).setHandler((asyncResult) -> {
                Object result;
                if (asyncResult.succeeded()) {
                    result = asyncResult.result();
                } else {
                    result = handleException(asyncResult.cause());
                }
                ResponseTools.endJson(routingContext.response(), result);
            });
        };
    }

    private ResponseDTO handleException(Throwable e) {
        if (e instanceof ErrcodeException) {
            ErrcodeException ex = (ErrcodeException) e;
            return ResponseDTO.of(ex);
        } else {
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
    }

    protected void routeJson(HttpMethod method, String url, Function<RoutingContext, Future<?>> hander) {
        getRouter().route(method, url).handler(wrap(hander));
    }
}
