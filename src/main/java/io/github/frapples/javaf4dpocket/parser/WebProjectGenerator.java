package io.github.frapples.javaf4dpocket.parser;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.frapples.javaf4dpocket.comm.response.ErrcodeException;
import io.github.frapples.javaf4dpocket.parser.model.DetectBaseVo;
import io.github.frapples.javaf4dpocket.parser.model.ModuleEntity;
import io.github.frapples.javaf4dpocket.parser.module.EntityFile;
import io.github.frapples.javaf4dpocket.parser.module.IGeneratedFile;
import io.github.frapples.javaf4dpocket.parser.module.MapperInterfaceFile;
import io.github.frapples.javaf4dpocket.parser.module.MapperXmlFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.Unchecked;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
@Singleton
public class WebProjectGenerator {

    @Inject
    private EntityFile entityFile;

    @Inject
    private MapperInterfaceFile mapperInterfaceFile;

    @Inject
    private MapperXmlFile mapperXmlFile;


    private List<IGeneratedFile> getGeneratedFiles() {
        return Arrays.asList(entityFile,mapperInterfaceFile, mapperXmlFile);
    }


    public void generate(ModuleEntity moduleEntity) {
        String rootPath = moduleEntity.getProject().getPath();
        if (!new File(rootPath).isDirectory()) {
            throw ErrcodeException.error(String.format("%s不存在或不是目录", rootPath));
        }

        for (IGeneratedFile<?> generatedFile : getGeneratedFiles()) {
            Map<String, Object> model = generatedFile.prepare(moduleEntity, null);
            Map<String, Object> m = ImmutableMap.<String, Object>builder()
                .putAll(model)
                .put("comment", moduleEntity.getTable().getTableComment())
                .put("author", moduleEntity.authorComment())
                .put("date", DateFormatUtils.format(new Date(), "yyyy-MM-dd"))
                .build();
            generatedFile.generate(moduleEntity, null, m);
        }
    }

    public List<DetectBaseVo> detect(String root) {
        JavaProjectParser parser = JavaProjectParser.of(root);
        Iterator<File> allJavaFiles = FileUtils.iterateFiles(new File(root), new String[]{"java", "xml"}, true);
        return Streams.stream(allJavaFiles)
            .filter(file -> file.length() < 10 * 1024 * 1024)
            .map(Unchecked.function((file) -> doDetect(file, parser)))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @SneakyThrows
    @Nullable
    private DetectBaseVo doDetect(File file, JavaProjectParser parser) {
        String path = file.getCanonicalPath();
        String context = FileUtils.readFileToString(file, Charsets.UTF_8);
        for (IGeneratedFile<?> generatedFile : getGeneratedFiles()) {
            return generatedFile.detect(parser, path, context);
        }
        return null;
    }
}
