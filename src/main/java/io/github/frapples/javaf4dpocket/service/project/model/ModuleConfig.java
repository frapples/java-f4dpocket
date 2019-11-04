package io.github.frapples.javaf4dpocket.service.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/9/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleConfig {

    private String type;

    private String tableName;

    private String entityClass;

    private String mapperInterface;

    private String mapperXml;

    private String serviceImplementation;

    private String serviceInterface;

    private String controller;
}
