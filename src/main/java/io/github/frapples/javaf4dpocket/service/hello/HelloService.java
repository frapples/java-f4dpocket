package io.github.frapples.javaf4dpocket.service.hello;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import io.github.frapples.javaf4dpocket.comm.base.BaseService;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/9/25
 */

@JsonRpcService
public class HelloService extends BaseService {

    @JsonRpcMethod
    public Map<String, Object> hello(@JsonRpcParam("word") String word) {
        Map<String, Object> map = new HashMap<>();
        map.put("word", word);
        return map;
    }
}
