package io.github.frapples.javaf4dpocket.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.frapples.javaf4dpocket.comm.base.BaseController;
import io.github.frapples.javaf4dpocket.comm.utils.guice.ComponentScanModule;
import io.github.frapples.javaf4dpocket.comm.utils.guice.ConfigureFileModule;
import java.util.Set;
import lombok.Data;
import spark.Spark;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class Application {

    @Inject
    private Set<BaseController> controllers;

    @Data
    @Singleton
    public static class ApplicationConfig {

        @Named("server.listening-address")
        @Inject
        private String listeningAddress;

        @Named("server.default-port")
        @Inject
        private int port;
    }

    @Inject
    ApplicationConfig config;


    private static Injector injector = Guice.createInjector(
        new ComponentScanModule("io.github.frapples", Singleton.class, javax.inject.Singleton.class),
        new ConfigureFileModule());

    public static <T> T getBean(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    public static void runAndAwaitInit(Integer port) {
        Application application = injector.getInstance(Application.class);
        application.startAndAwaitInit(port);
    }

    public static void main(String[] args) {
        runAndAwaitInit(null);
    }

    public static void stop() {
        Spark.stop();
    }

    private void startAndAwaitInit(Integer port) {
        if (port == null) {
            port = this.config.getPort();
        }
        Spark.port(port);
        Spark.ipAddress(config.getListeningAddress());
        Spark.threadPool(4, 2, 10 * 1000);
        controllers.forEach(BaseController::bind);
        Spark.awaitInitialization();
    }
}
