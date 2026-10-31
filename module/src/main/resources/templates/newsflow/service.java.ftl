package ${package.Service};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import com.yifan.newsflow.module.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ${table.serviceName} {
<#assign entityVar = entity?uncap_first>
<#assign mapperVar = table.mapperName?uncap_first>
<#assign idType = "Long">
<#list table.fields as field><#if field.keyFlag><#assign idType = field.propertyType></#if></#list>

    @Autowired
    private ${table.mapperName} ${mapperVar};

    public ${entity} getById(${idType} id) {
        log.info("查询记录，id={}", id);
        return ${mapperVar}.getById(id);
    }

    public ${entity} extractById(${idType} id) {
        log.info("提取记录，id={}", id);
        return ${mapperVar}.extractById(id);
    }

    public int update(${entity} ${entityVar}) {
        log.info("更新记录，id={}", ${entityVar}.getId());
        return ${mapperVar}.update(${entityVar});
    }

    public int insert(${entity} ${entityVar}) {
        log.info("插入记录，id={}", ${entityVar}.getId());
        return ${mapperVar}.insert(${entityVar});
    }

    public boolean delete(${idType} id) {
        log.info("删除记录，id={}", id);
        return ${mapperVar}.delete(id, TimeUtils.currentSeconds()) > 0;
    }

}
