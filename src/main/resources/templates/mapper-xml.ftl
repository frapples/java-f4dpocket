<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperInterface.package}.${mapperInterface.className}">
    <!-- table: ${tableName} -->

    <!-- Begin Base Config -->
    <!-- ALl Fields -->
    <sql id="fields">
        <#list mapperXml.fields! as v>${tableName}.${v.columnName}<#sep>, </#sep></#list>
    </sql>

    <sql id="values">
    <#list mapperXml.fields! as v>
        ${r"#{"}${v.javaColumnName}${r"}"}<#sep>,</#sep>
    </#list>
    </sql>

    <sql id="sets"><!-- Set Fields -->
        <trim suffixOverrides=",">
        <#list mapperXml.fields! as v>
            <if test="${v.javaColumnName} != null">${v.columnName} = ${r"#{"}${v.javaColumnName}${r"}"},</if>
        </#list>
        </trim>
    </sql>

    <!-- update sets -->
    <sql id="updateSets">
        <trim suffixOverrides=",">
    <#list mapperXml.fields! as v>
        <#if v.javaColumnName != 'id' && v.javaColumnName != 'addTime' && v.javaColumnName != 'updateTime'>
            <if test="${v.javaColumnName} != null">${v.columnName} = ${r"#{"}${v.javaColumnName}${r"}"},</if>
        </#if>
    </#list>
        </trim>
    </sql>

    <!-- batch update sets -->
    <sql id="batchUpdateSets">
        <trim prefix="set" suffixOverrides=",">
        <#list mapperXml.fields! as v>
            <#if v.javaColumnName != 'id' && v.javaColumnName != 'addTime' && v.javaColumnName != 'updateTime'>
            <trim prefix="${v.columnName} =case" suffix="end,">
                <foreach collection="list" item="item" index="index">
                    when id=${r"#{item.id}"}
                    <choose>
                        <when test="item.${v.javaColumnName} == null">
                            then ${v.columnName}
                        </when>
                        <otherwise>
                            then ${r"#{item."}${v.javaColumnName}${r"}"}
                        </otherwise>
                    </choose>
                </foreach>
            </trim>
            </#if>
        </#list>
        </trim>
    </sql>

    <!-- SQL查询条件明确，禁止conditions(便于索引) -->
    <sql id="conditions"><!-- Search Condition -->
        <trim prefix="where" prefixOverrides="and |or">
        <#list mapperXml.fields! as v>
            <if test="${v.javaColumnName} != null">and ${v.columnName} = ${r"#{"}${v.javaColumnName}${r"}"}</if>
        </#list>
        </trim>
    </sql>

    <!-- Fields to insert -->
    <sql id="insertFields">
        <trim suffixOverrides=",">
        <#list mapperXml.fields! as v>
            <#if v.javaColumnName != 'id'>
            <if test="${v.javaColumnName} != null">${v.columnName},</if>
            </#if>
        </#list>
        </trim>
    </sql>

    <!-- Values to insert -->
    <sql id="insertValues">
        <trim suffixOverrides=",">
        <#list mapperXml.fields! as v>
            <#if v.javaColumnName != 'id'>
            <if test="${v.javaColumnName} != null">${r"#{"}${v.javaColumnName}${r"}"},</if>
            </#if>
        </#list>
        </trim>
    </sql>

    <!-- Fields to batch insert -->
    <sql id="insertBatchFields">
        <trim suffixOverrides=",">
    <#list mapperXml.fields! as v>
        <#if v.javaColumnName != 'id'>
            <if test="obj.${v.javaColumnName} != null">${v.columnName},</if>
        </#if>
    </#list>
        </trim>
    </sql>

    <!-- batch insert values -->
    <sql id="insertBatchValues">
        <trim suffixOverrides=",">
    <#list mapperXml.fields! as v>
        <#if v.javaColumnName != 'id'>
            <if test="obj.${v.javaColumnName} != null">${r"#{obj."}${v.javaColumnName}${r"}"},</if>
        </#if>
    </#list>
        </trim>
    </sql>

    <!-- 分页查询后缀 -->
    <sql id="selectPageSuffix">
        limit
        ${r"#{pageBegin},"}
        ${r"#{pageSize}"}
    </sql>

    <!-- End Base Config
    ************************* 以上代码重新生成后会被替换，不允许修改 *************************
    -->

<#if mapperXml.custom.insert>
    <!-- 新增 -->
    <insert id="insert" parameterType="Object" useGeneratedKeys="true" keyProperty="id">
        insert into ${tableName}(<include refid="insertFields"/>)
        values(<include refid="insertValues"/>)
    </insert>
</#if>

<#if mapperXml.custom.update>
    <!-- 修改数据 -->
    <update id="update" parameterType="Object">
        update ${tableName} set
        <include refid="updateSets"/>
        where id = ${r"#{id}"}
    </update>
</#if>

<#if mapperXml.custom.selectEntities>
    <!-- 获取List<T>对象  (查询条件明确，禁止conditions) -->
    <select id="selectEntities" parameterType="Object" resultType="${pathClass}">
        select
        <include refid="fields"/>
        from ${tableName}
        <include refid="conditions"/>
        order by id
    </select>
</#if>

<#if mapperXml.custom.selectPage>
    <!-- 获取记录Count数量 -->
    <select id="selectPageCount" parameterType="Map" resultType="_long">
        select count(*) from ${tableName}
        <include refid="conditions"/>
    </select>
    <!-- 获取List<T>分页对象 -->
    <select id="selectPageEntities" parameterType="Map" resultType="${pathClass}">
        select
        <include refid="fields"/>
        from ${tableName} inner join (
        select id from ${tableName}
        <include refid="conditions"/>
        order by id desc
        <include refid="selectPageSuffix"/>
        ) as b on ${tableName}.id = b.id
    </select>
</#if>

<#if mapperXml.custom.insertBatch>
    <!-- 批量增加 -->
    <insert id="insertBatch" parameterType="List">
        <foreach collection="list" item="obj" separator=";">
            insert into ${tableName}(<include refid="insertBatchFields"/>)
            values(<include refid="insertBatchValues"/>)
        </foreach>
    </insert>
</#if>

<#if mapperXml.custom.selectById>
    <!-- 根据id查询对象 -->
    <select id="selectById" parameterType="_long" resultType="${pathClass}">
        select
        <include refid="fields"/>
        from ${tableName} where id = ${r"#{id}"} and mark = 0
    </select>
</#if>

<#if mapperXml.custom.updateBatch>
    <!-- 批量更新 -->
    <update id="updateBatch" parameterType="List">
        update ${tableName}
        <include refid="batchUpdateSets"/>
        <where>
            id in
            <foreach collection="list" open="(" separator="," close=")" item="item">
                ${r"#{item.id}"}
            </foreach>
        </where>
    </update>
</#if>

<#if mapperXml.custom.count>
    <!-- 计算数量 -->
    <select id="count" parameterType="Map" resultType="_long">
        select count(*) from ${tableName}
        <include refid="conditions"/>
    </select>
</#if>

<#if mapperXml.custom.exist>
    <!-- 查询是否存在 -->
    <select id="exist" parameterType="Map" resultType="Integer">
        select 1 from ${tableName}
        <include refid="conditions"/>
        limit 1
    </select>
</#if>
</mapper>