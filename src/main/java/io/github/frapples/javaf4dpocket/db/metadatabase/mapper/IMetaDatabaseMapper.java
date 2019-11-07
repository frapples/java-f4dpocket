package io.github.frapples.javaf4dpocket.db.metadatabase.mapper;

import io.github.frapples.javaf4dpocket.db.metadatabase.model.ColumnsEntity;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.TableEntity;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/4/17
 *
 *  1. 目前此类不能写xml，请注意
 *  2. 切勿直接注入此类, 否则会使用spring管理的sqlSessionFactory
 */
public interface IMetaDatabaseMapper {

    @Select("select table_comment from information_schema.tables"
            + " where table_schema = #{schemaName} AND table_name = #{tableName}")
    String getComment(@Param("schemaName") String schemaName, @Param("tableName") String tableName);

    @Select("select column_name, data_type, column_comment, column_type" + " from information_schema.columns"
        + " where table_schema = #{schemaName} AND table_name = #{tableName}")
    List<ColumnsEntity> getColumn(@Param("schemaName") String schemaName, @Param("tableName") String tableName);

    @Select("select table_name, table_comment from information_schema.tables where table_schema=#{schemaName} and table_type='base table'")
    List<TableEntity> getTables(@Param("schemaName") String schemaName);

}
