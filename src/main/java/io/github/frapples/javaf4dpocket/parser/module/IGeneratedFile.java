package io.github.frapples.javaf4dpocket.parser.module;

import io.github.frapples.javaf4dpocket.parser.JavaProjectParser;
import io.github.frapples.javaf4dpocket.parser.model.DetectBaseVo;
import io.github.frapples.javaf4dpocket.parser.model.ModuleEntity;
import java.util.List;
import java.util.Map;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/11
 */

public interface IGeneratedFile<T> {

    Map<String, Object> prepare(ModuleEntity module, T customConfig);

    List<String> generate(ModuleEntity module, T customConfig, Map<String, Object> model);

    DetectBaseVo detect(JavaProjectParser parser, String filePath, String fileContent);
}
