package io.github.frapples.javaf4dpocket.demo;

import java.util.List;
import java.util.Map;


/**
 * ORM基础接口
 *
 * @param <T>
 *            BaseEntity子类
 * @author cuiqf
 * @date 2013-12-9 上午09:56:37
 */
public interface BaseMapper<T> {
    /**
     * 插入一条记录
     * <p>
     * 说明：字段可选；忽略id；插入成功后可从entity获取id
     *
     * @param entity
     *            对象（需要设置操作人信息）
     * @return 影响行数；1: 成功新增一条记录；0: 插入失败
     */
    int insert(T entity);

    /**
     * 批量插入
     * <p>
     * 说明：字段可选；忽略id；插入成功后无法从对象获取id
     *
     * @param list
     *            对象集合（需要设置操作人信息）
     */
    void insertBatch(List<T> list);

    /**
     * 根据id更新对象
     * <p>
     * 说明：字段可选
     *
     * @param entity
     *            对象（需要设置操作人信息）
     * @return 影响行数；1: 成功更新一条记录；0: id为空或者更新失败
     */
    int update(T entity);

    /**
     * 根据id批量更新
     * <p>
     * 说明：字段可选
     *
     * @param list
     *            对象集合（需要设置操作人信息）
     * @return 成功更新的记录数
     */
    int updateBatch(List<T> list);

    /**
     * 分页查询 - 获取总记录数
     *
     * @param paramMap
     *            分页查询参数
     * @return 总记录数
     */
    long selectPageCount(Map<String, Object> paramMap);

    /**
     * 分页查询 - 获取当前记录集合
     *
     * @param paramMap
     *            分页查询参数
     * @return 当前记录集合
     */
    List<T> selectPageEntities(Map<String, Object> paramMap);

    /**
     * 查询对象集合
     *
     * @param paramMap
     *            查询参数（没有mark，默认设置mark为0）
     * @return 对象集合
     */
    List<T> selectEntities(Map<String, Object> paramMap);

    /**
     * 根据id查询对象
     * <p>
     * 说明：默认查询mark为0的
     *
     * @param id
     *            id
     * @return 对象
     */
    T selectById(long id);

    /**
     * 查询数量
     *
     * @param paramMap
     *            查询参数（没有mark，默认设置mark为0）
     * @return 数量
     */
    long count(Map<String, Object> paramMap);

    /**
     * 查询是否存在
     *
     * @param paramMap
     *            查询参数（没有mark，默认设置mark为0）
     * @return true: 存在, false: 不存在
     */
    Integer exist(Map<String, Object> paramMap);
}
