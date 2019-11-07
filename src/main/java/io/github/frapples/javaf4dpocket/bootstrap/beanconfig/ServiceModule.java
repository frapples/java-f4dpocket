package io.github.frapples.javaf4dpocket.bootstrap.beanconfig;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import io.github.frapples.javaf4dpocket.comm.base.BaseService;
import io.github.frapples.javaf4dpocket.service.generator.GeneratorService;
import io.github.frapples.javaf4dpocket.service.hello.HelloService;
import io.github.frapples.javaf4dpocket.service.project.ProjectService;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class ServiceModule extends AbstractModule {

    @Override
    public void configure() {
        Multibinder<BaseService> serviceBinder = Multibinder.newSetBinder(binder(), BaseService.class);
        serviceBinder.addBinding().to(HelloService.class);
        serviceBinder.addBinding().to(ProjectService.class);
        serviceBinder.addBinding().to(GeneratorService.class);
    }
}
