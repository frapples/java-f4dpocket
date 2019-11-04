package io.github.frapples.javaf4dpocket.db.metadatabase.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class ColumnsEntity implements Serializable {

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列类型
     */
    private String dataType;

    /**
     * 列注释
     */
    private String columnComment;

    /**
     * 列类型（例如：’int(11) unsigned‘）
     */
    private String columnType;
}
