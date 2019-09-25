package io.github.frapples.javaf4dpocket.bootstrap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.github.frapples.javaf4dpocket.comm.base.BaseController;
import io.github.frapples.javaf4dpocket.comm.base.BaseService;
import io.github.frapples.javaf4dpocket.comm.utils.JsonRpcDispatcher;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/9/25
 */
public class JsonrpcController extends BaseController {

    private ObjectMapper objectMapper = new ObjectMapper();

    private JsonRpcDispatcher jsonRpcDispatcher = new JsonRpcDispatcher(objectMapper);

    @Inject
    private Set<BaseService> services;

    @Override
    public void bind() {
        Spark.post("/api/json-rpc", this::jsonRpc);
        services.forEach(it -> jsonRpcDispatcher.addService("", it));
    }

    private Object jsonRpc(Request request, Response response) {
        return jsonRpcDispatcher.handle(request.body());
    }
}
