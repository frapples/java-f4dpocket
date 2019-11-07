package io.github.frapples.javaf4dpocket.comm.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Supplier;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
public class Beans {

    private static ObjectMapper jacksonMapper = JacksonUtils.jacksonObjectMapper();

    public static <T> T deepCopy(Object src, Class<T> dstSupplier) {
        return jacksonMapper.convertValue(src, dstSupplier);
    }

}
