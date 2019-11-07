package io.github.frapples.javaf4dpocket.parser.model;

import lombok.Data;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
@Data
public class MapperCustomEntity {

    boolean insert;

    boolean update;

    boolean delete;

    boolean selectEntities;

    boolean selectPage;

    boolean insertBatch;

    boolean selectById;

    boolean updateBatch;

    boolean count;

    boolean exist;

}
