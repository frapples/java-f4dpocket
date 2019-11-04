package io.github.frapples.javaf4dpocket.comm.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.github.arteam.simplejsonrpc.server.JsonRpcServer;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.SneakyThrows;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class JsonRpcDispatcher {

    private static final ErrorMessage METHOD_NOT_FOUND = new ErrorMessage(-32601, "Method not found");

    private ObjectMapper objectMapper;
    private JsonRpcServer jsonRpcServer;
    private ConcurrentMap<String, Object> services = new ConcurrentHashMap<>();

    public JsonRpcDispatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonRpcServer = JsonRpcServer.withMapper(objectMapper.copy()
            .setVisibility(PropertyAccessor.CREATOR, Visibility.ANY)
        );
    }

    public void addService(String beanName, Object service) {
        if (service.getClass().isAnnotationPresent(JsonRpcService.class)) {
            services.put(serviceName(beanName, service), service);
        }
    }

    private String serviceName(String beanName, Object service) {
        String simpleName = service.getClass().getSimpleName();
        return StringUtils.uncapitalize(StringUtils.removeEnd(simpleName, "Service"));
    }


    @SneakyThrows
    public @NotNull String handle(@NotNull String json) {
        // 稍微扩展json-rpc, 增加service字段
        JsonNode pack = objectMapper.readTree(json);
        JsonNode method = pack.get("method");
        if (method == null) {
            return error(pack, METHOD_NOT_FOUND);
        }

        List<String> r = Splitter.on(".").splitToList(method.asText());
        String serviceName = r.get(0);
        String methodName = r.get(1);
        ((ObjectNode)pack).put("method", methodName);

        if (!services.containsKey(serviceName)) {
            return error(pack, METHOD_NOT_FOUND);
        }
        Object service = services.get(serviceName);
        return this.jsonRpcServer.handle(objectMapper.writeValueAsString(pack), service);
    }

    public String handleBatch(String json) {
        throw new NotImplementedException("");
    }

    @SneakyThrows
    private String error(JsonNode node, ErrorMessage errorMessage) {
        Map<String, Object> error = ImmutableMap.of(
            "jsonrpc", node.path("jsonrpc").asText(),
            "id", node.path("id").asText(),
            "error", errorMessage);
        return objectMapper.writeValueAsString(error);
    }
}
