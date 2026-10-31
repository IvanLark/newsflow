package ${package.Entity};

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yifan.newsflow.module.util.TimeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("${table.name}")
public class ${entity} {

<#list table.fields as field><#if field.keyFlag>    @TableId(value = "${field.name}", type = IdType.AUTO)
</#if>    private ${field.propertyType} ${field.propertyName}<#if field.propertyName == "updateTime"> = TimeUtils.currentSeconds()</#if>;
</#list>
}
