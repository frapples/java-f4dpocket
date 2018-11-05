package io.github.frapples.javaf4dpocket.comm.tools;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class ResponseTools {

    private ResponseTools() {
        throw new UnsupportedOperationException();
    }

    public static void endJson(HttpServerResponse response, Object o) {
        response.putHeader("Content-Type", "text/json;charset=UTF-8");
        response.end(Json.encode(o));
    }
}
