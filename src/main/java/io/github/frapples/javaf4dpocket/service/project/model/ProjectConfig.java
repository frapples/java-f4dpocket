package io.github.frapples.javaf4dpocket.service.project.model;

import io.github.frapples.javaf4dpocket.db.metadatabase.model.DatabaseConfig;
import java.util.List;
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
public class ProjectConfig {

    private DatabaseConfig database;

    private List<AuthorConfig> authors;

    private List<ModuleConfig> modules;

}
