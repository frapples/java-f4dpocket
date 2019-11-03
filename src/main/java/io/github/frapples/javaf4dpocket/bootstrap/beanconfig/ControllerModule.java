package io.github.frapples.javaf4dpocket.bootstrap.beanconfig;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import io.github.frapples.javaf4dpocket.comm.base.BaseController;
import io.github.frapples.javaf4dpocket.bootstrap.controller.HttpController;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class ControllerModule extends AbstractModule {

    @Override
    public void configure() {
        Multibinder<BaseController> controllerBinder = Multibinder.newSetBinder(binder(), BaseController.class);
        controllerBinder.addBinding().to(HttpController.class);
    }
}
