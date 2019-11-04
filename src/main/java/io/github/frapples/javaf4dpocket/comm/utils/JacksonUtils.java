package io.github.frapples.javaf4dpocket.comm.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Frapples isfrapples@outlook.com
 * @date 2018/12/25
 */
public class JacksonUtils {

    public JacksonUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * 提供一个默秒全的objectMapper
     * 该mapper会对结果进行缩进
     * @return 返回一个新的ObjectMapper，做些默认的配置使其更符合一般习惯
     */
    public static ObjectMapper jacksonObjectMapperWithPretty() {
        return jacksonObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    }


    /**
     * 提供一个默秒全的objectMapper
     * 该mapper不对结果缩进
     * @return 返回一个新的ObjectMapper，做些默认的配置使其更符合一般习惯
     */
    public static ObjectMapper jacksonObjectMapper() {

        ObjectMapper mapper = new ObjectMapper();
        configureJavaTime(mapper);
        configureBase(mapper);
        configureSerializeNumberAsString(mapper);
        return mapper;
    }

    public static void configureBase(ObjectMapper mapper) {
        mapper
            .configure(MapperFeature.USE_STD_BEAN_NAMING, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.ALL, Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
            .setSerializationInclusion(Include.NON_NULL);
    }

    /**
     * 配置将所有的Number类型序列化为字符串
     * 请注意，如果Long类型虚拟化为json数字类型，被js接受后可能会丢失精度。
     * @param mapper
     */
    public static void configureSerializeNumberAsString(ObjectMapper mapper) {
        // 对高精度数字类型、long类型序列化为字符串
        mapper.registerModule(new SimpleModule()
            .addSerializer(BigDecimal.class, ToStringSerializer.instance)
            .addSerializer(BigInteger.class, ToStringSerializer.instance)
            .addSerializer(Long.class, ToStringSerializer.instance));
    }

    public static void configureJavaTime(ObjectMapper jackson2ObjectMapper) {
        jackson2ObjectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        /*
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        timeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        */

        jackson2ObjectMapper.registerModule(new SimpleModule()
            .addDeserializer(Date.class, new MultiDateDeserializer(
                Arrays.asList(
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
                ))));

        jackson2ObjectMapper
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            // 设置时区。注意，jackson这个坑逼不设置的话，默认值是UTC
            .setTimeZone(TimeZone.getDefault())
            /*
            .registerModule(timeModule)
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
             */
            ;
    }

    // 兼容@JsonFormat
    public static class MultiDateDeserializer extends DateDeserializer {
        private static final long serialVersionUID = 1L;

        private final List<String> dateFormats;

        public MultiDateDeserializer(List<String> dataFormats) {
            this.dateFormats = dataFormats;
        }

        @Override
        protected DateDeserializer withDateFormat(DateFormat df, String formatString) {
            List<String> dateFormats = new ArrayList<>();
            dateFormats.add(formatString);
            dateFormats.addAll(this.dateFormats);
            return new MultiDateDeserializer(dateFormats);
        }

        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            final String date = node.textValue();

            for (String dateFormat : dateFormats) {
                try {
                    return new SimpleDateFormat(dateFormat).parse(date);
                } catch (ParseException ignored) {
                }
            }
            if (date == null || date.isEmpty()) {
                return null;
            }
            throw new JsonParseException(jp, "Unparseable date: \"" + date + "\". Supported formats: " + dateFormats);
        }
    }
}
