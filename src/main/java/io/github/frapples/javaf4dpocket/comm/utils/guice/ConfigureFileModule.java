package io.github.frapples.javaf4dpocket.comm.utils.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.io.InputStream;
import java.util.Properties;
import lombok.SneakyThrows;

/**
 * @author Frapples
 *
 * // Thanks for https://stackoverflow.com/questions/41623295/guice-properties-injection
 */
public class ConfigureFileModule extends AbstractModule {

    private Class<?> loader;

    public ConfigureFileModule() {
        this(ConfigureFileModule.class);
    }

    public ConfigureFileModule(Class<?> loader) {
        this.loader = loader;
    }

    @Override
    @SneakyThrows
    protected void configure() {
        Properties properties = new Properties();
        try (InputStream in = loader.getResourceAsStream("/application.properties")) {
            properties.load(in);
        }
        String profile = properties.getProperty("profiles.active");
        if (profile != null && !profile.isEmpty()) {
            String name = String.format("/application-%s.properties", profile);
            try (InputStream in = loader.getResourceAsStream(name)) {
                properties.load(in);
            }
        }
        Names.bindProperties(binder(), properties);
    }
}