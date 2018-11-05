package io.github.frapples.javaf4dpocket.bootstrap;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.frapples.javaf4dpocket.controller.ControllerModule;
import io.vertx.core.Vertx;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class Application {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule(), new ControllerModule());
        Verticle verticle = injector.getInstance(Verticle.class);

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(verticle);
    }

    static class AppModule extends AbstractModule {

        @Override
        public void configure() {
        }
    }
}
