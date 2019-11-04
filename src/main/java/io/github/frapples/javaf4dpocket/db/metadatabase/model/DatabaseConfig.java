package io.github.frapples.javaf4dpocket.db.metadatabase.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/4/18
 */
@Data
@Accessors(chain = true)
public class DatabaseConfig {

    private String username;
    private String password;
    private String type;
    private String ipPort;
    private String dbname;

}
