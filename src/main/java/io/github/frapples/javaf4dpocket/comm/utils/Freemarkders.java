package io.github.frapples.javaf4dpocket.comm.utils;

import com.google.common.base.Suppliers;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.function.Supplier;
import lombok.SneakyThrows;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/9/26
 */
public class Freemarkders {

    public static Supplier<Configuration> freemarkerConfig = Suppliers.memoize(Freemarkders::freemarkerConfig);

    @SneakyThrows
    private static Configuration freemarkerConfig() {
        Configuration cfg = new Configuration();
        // 设置默认编码 (至关重要 - 解决中文乱码问题)
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateUpdateDelay(0);
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setClassForTemplateLoading(Freemarkders.class, "/");
        //cfg.setDirectoryForTemplateLoading(new File("."));
        return cfg;
    }

    public static String parse(String templateName, Map<String, Object> args) throws IOException, TemplateException {
        Template template = freemarkerConfig.get().getTemplate(templateName);
        StringWriter sw = new StringWriter();
        template.process(args, sw);
        return sw.toString();
    }

}
