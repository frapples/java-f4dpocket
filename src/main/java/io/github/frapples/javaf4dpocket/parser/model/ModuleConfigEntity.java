package io.github.frapples.javaf4dpocket.parser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.frapples.javaf4dpocket.comm.utils.PathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
@Data
public class ModuleConfigEntity {

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ModuleEntity root;

    private String srcPath;

    @JsonProperty("package")
    private String packageName;

    public String getFilePath() {
        String rootPath = root.getProject().getPath();
        return PathUtils.concat(rootPath, this.getSrcPath(), packageToDirectory(this.getPackageName()));
    }

    private String packageToDirectory(String packageName) {
        return StringUtils.replace(packageName, ".", "/");
    }

}
