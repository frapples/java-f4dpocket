package io.github.frapples.javaf4dpocket.bootstrap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.frapples.javaf4dpocket.bootstrap.SimpleRemoteLibraryCacheService;
import io.github.frapples.javaf4dpocket.bootstrap.SimpleRemoteLibraryCacheService.CDNResource;
import io.github.frapples.javaf4dpocket.comm.base.BaseController;
import io.github.frapples.javaf4dpocket.comm.base.BaseService;
import io.github.frapples.javaf4dpocket.comm.utils.JacksonUtils;
import io.github.frapples.javaf4dpocket.comm.utils.JsonRpcDispatcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/9/25
 */
@Singleton
public class HttpController extends BaseController {

    private ObjectMapper objectMapper = JacksonUtils.jacksonObjectMapperWithPretty();

    private JsonRpcDispatcher jsonRpcDispatcher = new JsonRpcDispatcher(objectMapper);

    @Inject
    private Set<BaseService> services;

    private SimpleRemoteLibraryCacheService simpleRemoteLibraryCacheService = new SimpleRemoteLibraryCacheService("./library-cache");

    @Override
    public void bind() {
        Spark.post("/api/json-rpc", this::jsonRpc);
        services.forEach(it -> jsonRpcDispatcher.addService("", it));

        Spark.get("/html/*", this::routePage);
        Spark.get("/", (request, response) -> {
            response.redirect("/html/main.html");
            return "";
        });

        Spark.get("cache", this::cacheLibrary);
        Spark.get("cache/*", this::cacheLibraryForCssFont);
    }

    private Object jsonRpc(Request request, Response response) throws InterruptedException {
        Thread.sleep(1000);
        return jsonRpcDispatcher.handle(request.body());
    }

    private Object routePage(Request request, Response response) throws IOException {
        String routePath = request.pathInfo().split("/html/")[1];
        response.type(this.contentType(routePath));
        try (InputStream in = readStaticFile("/public/" + routePath)) {
            ServletOutputStream o = response.raw().getOutputStream();
            IOUtils.copy(in, o);
        }
        return "";
    }

    private Object cacheLibrary(Request request, Response response) throws IOException {
        String url = request.queryParams("url");
        CDNResource cdnResource = simpleRemoteLibraryCacheService.get(url);
        InputStream in = cdnResource.getInputStream();
        ServletOutputStream o = response.raw().getOutputStream();
        IOUtils.copy(in, o);
        return "";
    }

    private Object cacheLibraryForCssFont(Request request, Response response) throws IOException {
        String s = request.url().substring("/cdn/".length());
        String url= "https://" + s;
        CDNResource cdnResource = simpleRemoteLibraryCacheService.get(url);
        InputStream in = cdnResource.getInputStream();
        ServletOutputStream o = response.raw().getOutputStream();
        IOUtils.copy(in, o);
        return "";
    }

    @SneakyThrows
    private InputStream readStaticFile(String path) {
        final boolean DEBUG = false;
        if (DEBUG) {
            String resourceDir = "E:\\project\\java-f4dpocket\\src\\main\\resources";
            return new FileInputStream(new File(resourceDir + path));
        } else {
            return getClass().getResourceAsStream(path);
        }
    }

    private String contentType(String routePath) {
        String extension = FilenameUtils.getExtension(routePath);
        Map<String, String> table = ImmutableMap.<String, String>builder()
            .put("js", "application/javascript; charset=UTF-8")
            .put("css", "text/css; charset=UTF-8")
            .put("html", "text/html; charset=UTF-8")
            .put("htm", "text/html; charset=UTF-8")
            .put("vue", "text/vue; charset=UTF-8")
            .build();
        String defaultType = "text/html; charset=UTF-8";
        return table.getOrDefault(extension, defaultType);
    }

}

