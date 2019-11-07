package io.github.frapples.javaf4dpocket.parser.model;

import io.github.frapples.javaf4dpocket.bootstrap.Application;
import io.github.frapples.javaf4dpocket.service.project.ProjectService;
import io.github.frapples.javaf4dpocket.service.project.model.ProjectConfig;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
@Data
public class ProjectEntity {

    private String name;

    private String code;

    private String path;

    @SneakyThrows
    public ProjectConfig getProjectConfig() {
        return Application.getBean(ProjectService.class).getProjectConfig(path);
    }
}
