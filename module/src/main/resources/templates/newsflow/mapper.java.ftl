package ${package.Mapper};

import ${package.Entity}.${entity};
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ${table.mapperName} {
<#assign entityVar = entity?uncap_first>
<#assign idType = "Long">
<#list table.fields as field><#if field.keyFlag><#assign idType = field.propertyType></#if></#list>

    @Select("SELECT <#list table.fields as field>${field.name}<#if field_has_next>, </#if></#list> FROM ${table.name} WHERE id = ${'#'}{id} AND is_deleted = 0")
    ${entity} getById(@Param("id") ${idType} id);

    @Select("SELECT <#list table.fields as field>${field.name}<#if field_has_next>, </#if></#list> FROM ${table.name} WHERE id = ${'#'}{id}")
    ${entity} extractById(@Param("id") ${idType} id);

    int update(@Param("${entityVar}") ${entity} ${entityVar});

    int insert(@Param("${entityVar}") ${entity} ${entityVar});

    @Update("UPDATE ${table.name} SET is_deleted = 1, update_time = ${'#'}{updateTime} WHERE id = ${'#'}{id} AND is_deleted = 0")
    int delete(@Param("id") ${idType} id, @Param("updateTime") Integer updateTime);

}
