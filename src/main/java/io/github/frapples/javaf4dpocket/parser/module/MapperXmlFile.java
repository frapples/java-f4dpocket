package io.github.frapples.javaf4dpocket.parser.module;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.frapples.javaf4dpocket.comm.response.ErrcodeException;
import io.github.frapples.javaf4dpocket.comm.utils.Freemarkders;
import io.github.frapples.javaf4dpocket.comm.utils.PathUtils;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.DatabaseConfig;
import io.github.frapples.javaf4dpocket.parser.JavaProjectParser;
import io.github.frapples.javaf4dpocket.parser.model.DetectBaseVo;
import io.github.frapples.javaf4dpocket.parser.model.MapperCustomEntity;
import io.github.frapples.javaf4dpocket.parser.model.ModuleConfigEntity;
import io.github.frapples.javaf4dpocket.parser.model.ModuleEntity;
import io.github.frapples.javaf4dpocket.parser.model.ProjectColumnEntity;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.Lombok;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jooq.lambda.Seq;
import org.jooq.lambda.fi.util.function.CheckedBiConsumer;
import org.jooq.lambda.function.Consumer2;
import org.jooq.lambda.function.Consumer3;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/11
 */
@Singleton
public class MapperXmlFile implements IGeneratedFile<MapperCustomEntity> {

    @Inject
    private EntityFile entityFile;


    @Override
    public Map<String, Object> prepare(ModuleEntity module, MapperCustomEntity mapperCustomEntity) {

        DatabaseConfig dbConfig = module.getProject().getProjectConfig().getDatabase();
        List<ProjectColumnEntity> fields = entityFile.entityFields(dbConfig, module.getTable().getTableName());

        return ImmutableMap.<String, Object>builder()
            .put("mapperXml", ImmutableMap.builder()
                .put("fields", fields)
                .put("custom", mapperCustomEntity)
                .build())
            .build();
    }

    @Override
    @SneakyThrows
    public List<String> generate(ModuleEntity module, MapperCustomEntity mapperCustomEntity, Map<String, Object> model) {
        String className = module.getModuleName() + "Mapper";
        ModuleConfigEntity config = module.getModules().get("mapperXml");
        if (config == null) {
            return Collections.emptyList();
        }

        String content = Freemarkders.parse("/templates/mapper-xml.ftl", model);
        FileUtils.forceMkdir(new File((config.getFilePath())));
        File file = new File(PathUtils.concat(config.getFilePath(), className + ".xml"));
        if (!file.exists()) {
            FileUtils.writeStringToFile(file, content, Charsets.UTF_8);
        } else {
            List<String> lines = FileUtils.readLines(file, Charsets.UTF_8);
            List<String> dstLines = IOUtils.readLines(new StringReader(content));
            Tuple2<Integer, Integer> srcMark = extractAutomaticallyGeneratedFragments(lines);
            Tuple2<Integer, Integer> dstMark = extractAutomaticallyGeneratedFragments(dstLines);

            Consumer3<OutputStream, List<String>, Boolean> writeHelper = ((out, list, isEnd) -> {
                try {
                    if (!list.isEmpty()) {
                        String context = String.join(System.lineSeparator(), list);
                        IOUtils.write(context, out, Charsets.UTF_8);
                        if (!isEnd) {
                            IOUtils.write(System.lineSeparator(), out, Charsets.UTF_8);
                        }
                    }
                } catch (Exception e) {
                    throw Lombok.sneakyThrow(e);
                }
            });

            try (OutputStream out = FileUtils.openOutputStream(file)) {
                writeHelper.accept(out, lines.subList(0, srcMark.v1), false);
                writeHelper.accept(out, dstLines.subList(dstMark.v1, dstMark.v2), false);
                writeHelper.accept(out, lines.subList(srcMark.v2, lines.size()), true);
            }
        }

        return Collections.singletonList(file.getCanonicalPath());
    }

    @SneakyThrows
    private Tuple2<Integer, Integer> extractAutomaticallyGeneratedFragments(List<String> lines) {
        String splitter = "************************* 以上代码重新生成后会被替换，不允许修改 *************************";
        int splitLineNo = (int) (Seq.seq(lines).zipWithIndex()
                    .filter(((tuple) -> tuple.v1.contains(splitter))).map(Tuple2::v2).findFirst()
                    .orElseThrow(() -> ErrcodeException.error("错误"))
                    + 1);
        return Tuple.tuple(0, splitLineNo + 1);
    }

    @Data
    public static class MapperDetectVo extends DetectBaseVo {

        private String mapperInterfaceClassName;

        private String mapperInterfacePath;

        private String dbTableName;
    }


    @Override
    @SneakyThrows
    public DetectBaseVo detect(JavaProjectParser parser, String filePath, String fileContent) {
        if (!FilenameUtils.getExtension(filePath).toLowerCase().equals("xml")) {
            return null;
        }

        MapperDetectVo mapperDetectVo = new MapperDetectVo();
        File file = new File(filePath);

        boolean find = false;
        try (InputStream in = FileUtils.openInputStream(file)) {
            for (PeekingIterator<String> it = Iterators.peekingIterator(IOUtils.lineIterator(in, Charsets.UTF_8.name())); it.hasNext(); ) {
                String line = it.next();

                Pattern pattern = Pattern.compile(".*<mapper\\s+namespace=\"(.+)\".*");
                Matcher m = pattern.matcher(line.trim());
                if (m.matches()) {
                    find = true;
                    mapperDetectVo.mapperInterfaceClassName = m.group(1);
                    mapperDetectVo.setPath(file.getPath());

                    String nextLine = it.peek();
                    Pattern patternComment = Pattern.compile(".*<!--\\s*table: ([a-zA-Z-_]+)\\s*-->.*");
                    Matcher m2 = patternComment.matcher(nextLine);
                    if (m2.matches()) {
                        mapperDetectVo.dbTableName = m2.group(1);
                    }

                    break;
                }
            }
            mapperDetectVo = find ? mapperDetectVo : null;

            if (mapperDetectVo != null) {
                mapperDetectVo.mapperInterfacePath = parser.javaFilePathByFullClassName(mapperDetectVo.mapperInterfaceClassName);
            }
            return mapperDetectVo;
        }
    }
}
