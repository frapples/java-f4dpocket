package io.github.frapples.javaf4dpocket.parser.module;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.frapples.javaf4dpocket.comm.utils.Beans;
import io.github.frapples.javaf4dpocket.comm.utils.Freemarkders;
import io.github.frapples.javaf4dpocket.comm.utils.PathUtils;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.ColumnsEntity;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.DatabaseConfig;
import io.github.frapples.javaf4dpocket.db.metadatabase.service.MetaDatabaseService;
import io.github.frapples.javaf4dpocket.parser.JavaProjectParser;
import io.github.frapples.javaf4dpocket.parser.model.DetectBaseVo;
import io.github.frapples.javaf4dpocket.parser.model.ModuleConfigEntity;
import io.github.frapples.javaf4dpocket.parser.model.ModuleEntity;
import io.github.frapples.javaf4dpocket.parser.model.ProjectColumnEntity;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/11
 */
@Singleton
public class EntityFile implements IGeneratedFile<Void> {

    @Inject
    private MetaDatabaseService metaDatabaseService;

    @Override
    public Map<String, Object> prepare(ModuleEntity module, Void customConfig) {
        String className = module.getModuleName() + "Entity";
        ModuleConfigEntity config = module.getModules().get("entity");
        if (config == null) {
            return null;
        }

        DatabaseConfig dbConfig = module.getProject().getProjectConfig().getDatabase();
        List<ProjectColumnEntity> fields = entityFields(dbConfig, module.getTable().getTableName());
        boolean importBigDecimal = fields.stream().map(ProjectColumnEntity::getJavaType).anyMatch(Predicate.isEqual("BigDecimal"));
        boolean importDate = fields.stream().map(ProjectColumnEntity::getJavaType).anyMatch(Predicate.isEqual("Date"));

        return ImmutableMap.<String, Object>builder()
            .put("entity", ImmutableMap.builder()
                .put("package", config.getPackageName())
                .put("className", className)
                .put("fields", fields)
                .put("imports", ImmutableMap.builder()
                    .put("bigDecimal", importBigDecimal)
                    .put("date", importDate)
                    .build())
                .build())
            .build();
    }

    @Override
    @SneakyThrows
    public List<String> generate(ModuleEntity module, Void customConfig, Map<String, Object> model) {
        String content = Freemarkders.parse("/templates/entity.ftl", model);
        ModuleConfigEntity config = module.getModules().get("entity");
        if (config == null) {
            return Collections.emptyList();
        }

        String className = module.getModuleName() + "Entity";
        FileUtils.forceMkdir(new File(config.getFilePath()));
        File file = new File(PathUtils.concat(config.getFilePath(), className + ".java"));
        FileUtils.writeStringToFile(file, content, Charsets.UTF_8);
        return Collections.singletonList(file.getCanonicalPath());
    }


    public static class EntityDetectVo extends DetectBaseVo {

        private String dbTableName;
    }

    @Override
    @SneakyThrows
    public DetectBaseVo detect(JavaProjectParser parser, String filePath, String fileContent) {
        if (!FilenameUtils.getExtension(filePath).toLowerCase().equals("java")) {
            return null;
        }

        EntityDetectVo entityDetectVo = new EntityDetectVo();
        boolean find = false;
        for (String line : Splitter.onPattern("\r?\n").split(fileContent)) {
            Pattern patternComment = Pattern.compile(".*\\s*table: ([a-zA-Z-_]+)\\s*.*");
            Matcher m2 = patternComment.matcher(line);
            if (m2.matches()) {
                find = true;
                entityDetectVo.setPath(filePath);
                entityDetectVo.dbTableName = m2.group(1);
                String fullClassName = JavaProjectParser.parseFullClassName(new File(filePath));
                entityDetectVo.setFullClassName(fullClassName);
            }
        }
        return find ? entityDetectVo : null;
    }


    public List<ProjectColumnEntity> entityFields(DatabaseConfig databaseConfig, String tableName) {
        List<ColumnsEntity> fields = metaDatabaseService.getColumn(databaseConfig, tableName);
        List<ProjectColumnEntity> entities = fields.stream().map((filed) -> {
            ProjectColumnEntity entity = Beans.deepCopy(filed, ProjectColumnEntity.class);
            if (entity.getColumnName().contains("_")) {
                entity.setJavaColumnName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, entity.getColumnName()));
            } else {
                entity.setJavaColumnName(entity.getColumnName());
            }
            entity.setJavaType(toJavaType(entity.getDataType(), entity.getColumnType()));
            return entity;
        }).collect(Collectors.toList());
        return entities;
    }

    /**
     * 数据库字段类型和java字段类型映射
     */
    private static final Map<String, String> DBTYPE_TO_JAVATYPE_MAP = ImmutableMap.<String, String>builder()
        // Integer
        .put("int", "Integer")
        .put("tinyint", "Integer")
        .put("smallint", "Integer")
        .put("mediumint", "Integer")
        .put("integer", "Integer")
        // Long
        .put("bigint", "Long")
        // BigDecimal
        .put("decimal", "BigDecimal")
        .put("double", "Double")
        .put("float", "Float")
        .put("numeric", "BigDecimal")
        // String
        .put("varchar", "String")
        .put("char", "String")
        .put("text", "String")
        .put("tinytext", "String")
        .put("mediumtext", "String")
        .put("longtext", "String")
        .put("enum", "String")
        // LocalDateTime、LocalDate在某些数据库连接池下不支持
        .put("datetime", "Date")
        .put("timestamp", "Date")
        .put("date", "Date")
        // byte[]
        .put("blob", "byte[]")
        .build();

    public static String toJavaType(String fieldType, String columnType) {
        if (fieldType == null || columnType == null) {
            throw new IllegalArgumentException();
        }

        // int unsigned类型对应Long
        if (columnType.contains("unsigned") && "int".equals(fieldType)) {
            return "Long";
        }

        String resultType = DBTYPE_TO_JAVATYPE_MAP.get(fieldType.toLowerCase());
        if (resultType == null) {
            throw new IllegalArgumentException();
        }
        return resultType;
    }

}
