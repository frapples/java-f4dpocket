package io.github.frapples.javaf4dpocket.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.frapples.javaf4dpocket.comm.base.BaseController;
import io.github.frapples.javaf4dpocket.comm.utils.guice.ComponentScanModule;
import io.github.frapples.javaf4dpocket.comm.utils.guice.ConfigureFileModule;
import java.util.Set;
import spark.Spark;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class Application {

    @Inject
    private Set<BaseController> controllers;

    @Named("server.default-port")
    @Inject
    private int port;

    private static Injector injector;

    public static <T> T getBean(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    public static void run(Integer port) {
        injector = Guice.createInjector(
            new ComponentScanModule("io.github.frapples", Singleton.class, javax.inject.Singleton.class),
            new ConfigureFileModule());
        Application application = injector.getInstance(Application.class);
        application.start(port);
    }

    public static void main(String[] args) {
        run(null);
    }

    private void start(Integer port) {
        if (port == null) {
            port = this.port;
        }
        Spark.port(port);
        controllers.forEach(BaseController::bind);
    }
}
