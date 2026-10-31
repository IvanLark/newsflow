<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">
<#assign entityVar = entity?uncap_first>

    <insert id="insert" useGeneratedKeys="true" keyProperty="${entityVar}.id">
        INSERT INTO ${table.name}(
<#list table.fields as field><#if !field.keyFlag && field.name != "is_deleted">        <if test="${entityVar}.${field.propertyName} != null<#if field.propertyType == "String"> and ${entityVar}.${field.propertyName} != ''</#if>">${field.name},</if>
</#if></#list>        is_deleted
        ) VALUES (
<#list table.fields as field><#if !field.keyFlag && field.name != "is_deleted">        <if test="${entityVar}.${field.propertyName} != null<#if field.propertyType == "String"> and ${entityVar}.${field.propertyName} != ''</#if>">${'#'}{${entityVar}.${field.propertyName}},</if>
</#if></#list>        ${'#'}{${entityVar}.isDeleted}
        )
    </insert>

    <update id="update">
        UPDATE ${table.name}
        <set>
<#list table.fields as field><#if !field.keyFlag && field.name != "is_deleted" && field.name != "create_time">            <if test="${entityVar}.${field.propertyName} != null<#if field.propertyType == "String"> and ${entityVar}.${field.propertyName} != ''</#if>">${field.name} = ${'#'}{${entityVar}.${field.propertyName}},</if>
</#if></#list>            is_deleted = ${'#'}{${entityVar}.isDeleted}
        </set>
        WHERE id = ${'#'}{${entityVar}.id}
    </update>

</mapper>
