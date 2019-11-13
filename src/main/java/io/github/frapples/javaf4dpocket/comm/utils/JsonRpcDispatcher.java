package io.github.frapples.javaf4dpocket.comm.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.googlecode.jsonrpc4j.DefaultErrorResolver;
import com.googlecode.jsonrpc4j.JsonRpcInterceptor;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.JsonRpcService;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class JsonRpcDispatcher {

    private ObjectMapper objectMapper;
    private ObjectMapper jsonRpcObjectMapper;
    private ConcurrentMap<String, JsonRpcServer> jsonRpcServers = new ConcurrentHashMap<>();

    public JsonRpcDispatcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonRpcObjectMapper = objectMapper.copy()
            .setVisibility(PropertyAccessor.CREATOR, Visibility.ANY);
    }

    public void addService(String beanName, Object service) {
        String name = serviceName(beanName, service);

        JsonRpcServer jsonRpcServer = new JsonRpcServer(jsonRpcObjectMapper, service, service.getClass());
        jsonRpcServers.put(name, jsonRpcServer);
    }

    private String serviceName(String beanName, Object service) {
        String simpleName = service.getClass().getSimpleName();
        return StringUtils.uncapitalize(StringUtils.removeEnd(simpleName, "Service"));
    }


    @SneakyThrows
    public @NotNull String handle(@NotNull String json) {
        JsonNode pack = objectMapper.readTree(json);
        JsonNode method = pack.get("method");
        if (method == null) {
            error(pack);
        }

        List<String> r = Splitter.on(".").splitToList(method.asText());
        String serviceName = r.get(0);
        String methodName = r.get(1);
        ((ObjectNode)pack).put("method", methodName);

        if (!jsonRpcServers.containsKey(serviceName)) {
            return error(pack);
        }
        ByteArrayOutputStream w = new ByteArrayOutputStream();
        jsonRpcServers.get(serviceName).handleRequest(IOUtils.toInputStream(objectMapper.writeValueAsString(pack), Charsets.UTF_8), w);
        return w.toString(Charsets.UTF_8.name());
    }

    public String handleBatch(String json) {
        throw new NotImplementedException("");
    }

    @SneakyThrows
    private String error(JsonNode node) {
        Map<String, Object> error = ImmutableMap.of(
            "jsonrpc", node.path("jsonrpc").asText(),
            "id", node.path("id").asText(),
            "error", ImmutableMap.of(
                "code", -32001,
                "message", "未找到服务")
        );
        return objectMapper.writeValueAsString(error);
    }
}
