package io.github.frapples.javaf4dpocket.parser.model;

import io.github.frapples.javaf4dpocket.db.metadatabase.model.ColumnsEntity;
import lombok.Data;

@Data
public class ProjectColumnEntity extends ColumnsEntity {

    /**
     * 排序
     */
    private Integer orderNum;


    /**
     * Java属性
     */
    private String javaColumnName;

    /**
     * JAVA类型
     */
    private String javaType;

}
