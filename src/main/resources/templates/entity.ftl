package ${entity.package};

<#if entity.imports.bigDecimal>
import java.math.BigDecimal;
</#if>
<#if entity.imports.date>
import java.time.*;
</#if>
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * ${comment}Entity
 *
 * @author ${author}
 * @date ${date}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ${entity.className} {
<#list entity.fields! as v>
    /**
     * ${v.columnComment}
     */
    private ${v.javaType} ${v.javaColumnName};

</#list>
}