package io.github.frapples.javaf4dpocket.bootstrap;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.github.frapples.javaf4dpocket.comm.base.BaseController;
import io.github.frapples.javaf4dpocket.bootstrap.beanconfig.ControllerModule;
import io.github.frapples.javaf4dpocket.bootstrap.beanconfig.ServiceModule;
import java.util.Set;
import spark.Spark;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class Application {

    @Inject
    private Set<BaseController> controllers;

    private static Injector injector;

    public static <T> T getBean(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    public static void main(String[] args) {
        injector = Guice.createInjector(
            new AppModule(),
            new ControllerModule(),
            new ServiceModule()
        );
        Application application = injector.getInstance(Application.class);
        application.init();
    }

    public void init() {
        Spark.port(getPort());
        controllers.forEach(BaseController::bind);
    }

    private int getPort() {
        try {
            return Integer.parseInt(System.getProperty("port"));
        } catch (Exception e) {
            return 8080;
        }
    }

    static class AppModule extends AbstractModule {

        @Override
        public void configure() {
        }
    }
}
