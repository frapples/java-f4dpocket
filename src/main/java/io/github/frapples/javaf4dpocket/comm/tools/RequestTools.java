package io.github.frapples.javaf4dpocket.comm.tools;

import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.frapples.javaf4dpocket.comm.utils.ValidationUtils;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import java.util.function.Function;
import lombok.SneakyThrows;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class RequestTools {

    private static Gson gson = new Gson();

    private RequestTools() {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    public static <T, E extends RuntimeException> Future<T> decodeAndValid(
        RoutingContext context, Class<T> clazz, Function<String, E> exceptionNew) {
        return bodyBuffer(context.request())
            .compose((buffer) -> {
                try {
                    // 自带的json太严格了，不允许多余的字段
                    // Json.decodeValue(buffer, clazz))
                    JsonObject json = MoreObjects.firstNonNull(
                        gson.fromJson(buffer.toString(), JsonObject.class),
                        new JsonObject());
                    // 由于query string都是字符串，如果直接用反射则涉及到很多字符串转具体类型的代码。这里利用了gson来间接做到
                    context.request().params().forEach(
                        e -> json.addProperty(e.getKey(), e.getValue()));
                    T o = gson.fromJson(json, clazz);
                    ValidationUtils.validOrThrow(o, exceptionNew);
                    return Future.succeededFuture(o);
                } catch (Exception e) {
                    return Future.failedFuture(e);
                }
            });
    }

    private static Future<Buffer> bodyBuffer(HttpServerRequest request) {
        Future<Buffer> future = Future.future();
        request.bodyHandler(future::complete);
        return future;
    }

}
