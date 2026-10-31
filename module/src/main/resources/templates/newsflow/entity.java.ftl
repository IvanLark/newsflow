package ${package.Entity};

import com.yifan.newsflow.module.utils.TimeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ${entity} {

<#list table.fields as field>    private ${field.propertyType} ${field.propertyName}<#if field.propertyName == "updateTime"> = TimeUtils.currentSeconds()</#if>;
</#list>}
