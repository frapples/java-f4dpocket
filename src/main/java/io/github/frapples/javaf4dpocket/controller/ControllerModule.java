package io.github.frapples.javaf4dpocket.controller;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import io.github.frapples.javaf4dpocket.controller.base.BaseController;
import io.github.frapples.javaf4dpocket.controller.hello.HelloController;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class ControllerModule extends AbstractModule {

    @Override
    public void configure() {
        Multibinder<BaseController> controllerBinder = Multibinder.newSetBinder(binder(), BaseController.class);
        controllerBinder.addBinding().to(HelloController.class);
    }
}
