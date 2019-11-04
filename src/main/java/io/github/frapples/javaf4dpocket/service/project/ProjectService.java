package io.github.frapples.javaf4dpocket.service.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.google.common.base.Charsets;
import io.github.frapples.javaf4dpocket.comm.base.BaseService;
import io.github.frapples.javaf4dpocket.comm.utils.JacksonUtils;
import io.github.frapples.javaf4dpocket.comm.utils.PathUtils;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.DatabaseConfig;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.TableEntity;
import io.github.frapples.javaf4dpocket.db.metadatabase.service.MetaDatabaseService;
import io.github.frapples.javaf4dpocket.parser.SimpleJavaProjectParser;
import io.github.frapples.javaf4dpocket.service.project.model.ProjectConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Data;
import org.apache.commons.io.FileUtils;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/9/26
 */
@JsonRpcService
public class ProjectService extends BaseService {

    private final String CONFIG_FILE_NAME = "f4dpocket-project.json";

    @Inject
    private MetaDatabaseService metaDatabaseService;
    private ObjectMapper objectMapper = JacksonUtils.jacksonObjectMapperWithPretty();

    @JsonRpcMethod
    public ProjectConfig getProjectConfig(@JsonRpcParam("path") String directory) throws IOException {
        return readConfig(directory);
    }

    @JsonRpcMethod
    public void updateProjectConfig(@JsonRpcParam("path") String directory, @JsonRpcParam("database") DatabaseConfig databaseConfig) throws IOException {
        ProjectConfig config = readConfig(directory);
        config.setDatabase(databaseConfig);
        writeConfig(directory, config);
    }

    @JsonRpcMethod
    public void generateCode(@JsonRpcParam("path") String directory) throws IOException {
        ProjectConfig config = readConfig(directory);
    }

    @JsonRpcMethod
    public Object testDatabaseConnection(@JsonRpcParam("path") String directory) throws IOException {
        ProjectConfig projectConfig = readConfig(directory);

        @Data
        class Vo {
            boolean success;
            String reason;
        }

        Vo vo = new Vo();
        try {
            metaDatabaseService.test(projectConfig.getDatabase());
            vo.success = true;
        } catch (Exception e) {
            vo.success = false;
            vo.reason = e.getMessage();
        }
        return vo;
    }

    @JsonRpcMethod
    public List<TableEntity> getDatabaseTables(@JsonRpcParam("path") String directory) throws IOException {
        ProjectConfig projectConfig = readConfig(directory);
        List<TableEntity> tables = metaDatabaseService.getTables(projectConfig.getDatabase());
        return tables.stream().sorted(Comparator.comparing(TableEntity::getTableName)).collect(Collectors.toList());
    }

    @JsonRpcMethod
    public Set<String> allTopJavaClassFullNames(@JsonRpcParam("path") String directory) {
        SimpleJavaProjectParser parser = SimpleJavaProjectParser.of(directory);
        parser.parse();
        return parser.getJavaClassNames();
    }

    @JsonRpcMethod
    public Set<String> allPackageNames(@JsonRpcParam("path") String directory) {
        SimpleJavaProjectParser parser = SimpleJavaProjectParser.of(directory);
        parser.parse();
        return parser.getPackageNames();
    }

    private void writeConfig(String directory, ProjectConfig projectConfig) throws IOException {
        String path = PathUtils.concat(directory, CONFIG_FILE_NAME);
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }

        String json = objectMapper.writeValueAsString(projectConfig);
        FileUtils.writeStringToFile(file, json, Charsets.UTF_8);
    }

    private ProjectConfig readConfig(String directory) throws IOException {
        String path = PathUtils.concat(directory, CONFIG_FILE_NAME);
        File file = new File(path);
        if (file.exists()) {
            String json = FileUtils.readFileToString(file, Charsets.UTF_8);
            return objectMapper.readValue(json, ProjectConfig.class);
        } else {
            return defaultConfig();
        }
    }

    private ProjectConfig defaultConfig() {
        return ProjectConfig.builder()
            .database(new DatabaseConfig())
            .modules(new ArrayList<>())
            .build();
    }
}
