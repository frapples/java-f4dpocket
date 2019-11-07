package io.github.frapples.javaf4dpocket.parser.module;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import io.github.frapples.javaf4dpocket.comm.utils.Freemarkders;
import io.github.frapples.javaf4dpocket.comm.utils.PathUtils;
import io.github.frapples.javaf4dpocket.parser.JavaProjectParser;
import io.github.frapples.javaf4dpocket.parser.model.DetectBaseVo;
import io.github.frapples.javaf4dpocket.parser.model.ModuleConfigEntity;
import io.github.frapples.javaf4dpocket.parser.model.ModuleEntity;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/11
 */
@Singleton
public class MapperInterfaceFile implements IGeneratedFile<Void> {

    @Override
    public Map<String, Object> prepare(ModuleEntity module, Void customConfig) {
        String className = module.getModuleName() + "Mapper";
        ModuleConfigEntity config = module.getModules().get("mapperInterface");
        return ImmutableMap.<String, Object>builder()
            .put("mapperInterface", ImmutableMap.builder()
                .put("package", config.getPackageName())
                .put("className", className)
                .build())
            .build();
    }

    @Override
    @SneakyThrows
    public List<String> generate(ModuleEntity module, Void customConfig, Map<String, Object> model) {
        String className = module.getModuleName() + "Mapper";
        ModuleConfigEntity config = module.getModules().get("mapperInterface");
        if (config == null) {
            return Collections.emptyList();
        }

        String content = Freemarkders.parse("/templates/mapper.ftl", model);
        FileUtils.forceMkdir(new File((config.getFilePath())));
        String filePath = PathUtils.concat(config.getFilePath(), className + ".java");
        FileUtils.writeStringToFile(new File(filePath), content, Charsets.UTF_8);
        return Collections.singletonList(filePath);
    }

    @Override
    public DetectBaseVo detect(JavaProjectParser parser, String filePath, String fileContent) {
        return null;
    }
}
