package io.github.frapples.javaf4dpocket.comm.utils.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

/**
 * @author Frapples
 *
 * Thanks for https://stackoverflow.com/questions/30313403/auto-scan-for-guice
 */
public final class ComponentScanModule extends AbstractModule {
    private final String packageName;
    private final Set<Class<? extends Annotation>> bindingAnnotations;


    @SafeVarargs
    public ComponentScanModule(String packageName,
        final Class<? extends Annotation>... bindingAnnotations) {
        this.packageName = packageName;
        this.bindingAnnotations = new HashSet<>(Arrays.asList(bindingAnnotations));
    }

    @Override
    public void configure() {
        Map<Class, Multibinder> multiBinders = new HashMap<>();

        Reflections packageReflections = new Reflections(packageName);
        bindingAnnotations.stream()
            .map(packageReflections::getTypesAnnotatedWith)
            .flatMap(Set::stream)
            .forEach(bean -> {
                Set<Class<?>> types = ReflectionUtils.getSuperTypes(bean);
                for (Class<?> type : types) {
                    Multibinder binder = multiBinders.computeIfAbsent(type, t -> Multibinder.newSetBinder(binder(), type));
                    binder.addBinding().to(bean);
                }
            });
        multiBinders.values();
    }
}