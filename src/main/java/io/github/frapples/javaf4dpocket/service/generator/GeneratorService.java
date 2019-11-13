package io.github.frapples.javaf4dpocket.service.generator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import freemarker.template.TemplateException;
import io.github.frapples.javaf4dpocket.comm.base.BaseService;
import io.github.frapples.javaf4dpocket.parser.WebProjectGenerator;
import io.github.frapples.javaf4dpocket.parser.model.DetectBaseVo;
import io.github.frapples.javaf4dpocket.parser.model.ModuleEntity;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
@Singleton
public class GeneratorService extends BaseService {

    @Inject
    private WebProjectGenerator webProjectGenerator;

    public void generate(@JsonRpcParam("param") ModuleEntity moduleEntity) throws IOException, TemplateException {
        webProjectGenerator.generate(moduleEntity);
    }

    public List<DetectBaseVo> detectAllModules(@JsonRpcParam("path") String path) {
        return webProjectGenerator.detect(path);
    }

    public void regenerate(@JsonRpcParam("config") Map<String, Object> moduleEntity) {
        System.out.println(moduleEntity);
    }
}
