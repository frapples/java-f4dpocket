package io.github.frapples.javaf4dpocket.parser.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.github.frapples.javaf4dpocket.comm.utils.PathUtils;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.TableEntity;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
@Data
public class ModuleEntity {

    private String moduleName;

    private ProjectEntity project;

    private AuthorEntity author;

    private TableEntity table;

    private Map<String, ModuleConfigEntity> modules;

    @JsonSetter
    public void setModules(Map<String, ModuleConfigEntity> modules) {
        modules.values().forEach(moduleConfigEntity -> moduleConfigEntity.setRoot(this));
        this.modules = modules;
    }

    public String authorComment() {
        if (!StringUtils.isBlank(getAuthor().getEmail())) {
            return String.format("%s <%s>", getAuthor().getNickName(), getAuthor().getEmail());
        } else {
            return getAuthor().getNickName();
        }
    }


}
