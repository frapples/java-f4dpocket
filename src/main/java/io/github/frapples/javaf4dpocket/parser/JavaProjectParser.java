package io.github.frapples.javaf4dpocket.parser;

import com.google.common.base.Splitter;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.Unchecked;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/5/22
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaProjectParser {

    private String root;

    private Supplier<Map<String, String>> javaClassesSupplier = Suppliers.memoize(this::parse);

    private static Cache<String, JavaProjectParser> cache = CacheBuilder.newBuilder()
        .maximumSize(10)
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build();


    @SneakyThrows
    public static JavaProjectParser of(String root) {
        return cache.get(root, () -> {
            JavaProjectParser parser = new JavaProjectParser();
            parser.root = root;
            return parser;
        });
    }

    @SneakyThrows
    public Map<String, String> parse() {
        Collection<File> allJavaFiles = FileUtils.listFiles(new File(this.root), new String[]{"java"}, true);
        TreeMap<String, String> javaClasses = new TreeMap<>();
        for (File javaFile : allJavaFiles) {
            String fullClassName = parseFullClassName(javaFile);
            if (fullClassName != null) {
                javaClasses.put(fullClassName, javaFile.getCanonicalPath());
            }
        }
        return javaClasses;
    }

    @SneakyThrows
    public static String parseFullClassName(File file) {
        Pattern pattern = Pattern.compile("package\\s+([^;]+);");
        String javaClass = FilenameUtils.getBaseName(file.getName());

        AtomicReference<String> fullClassName = new AtomicReference<>();
        Files.asCharSource(file, StandardCharsets.UTF_8).forEachLine(Unchecked.consumer((line) -> {
            Matcher m = pattern.matcher(line.trim());
            if (m.matches()) {
                fullClassName.set(m.group(1) + "." + javaClass);
            }
        }));
        return fullClassName.get();
    }

    public Set<String> getPackageNames() {
        return getJavaClassNames().stream().map(fullName -> {
            String reversedFullName = StringUtils.reverse(fullName);
            String reversedPackageName = Iterables.getLast(Splitter.on(".").limit(1).split(reversedFullName));
            return StringUtils.reverse(reversedPackageName);
        }).collect(Collectors.toCollection(TreeSet::new));
    }

    public Set<String> getJavaClassNames() {
        return Collections.unmodifiableSet(javaClassesSupplier.get().keySet());
    }

    public String javaFilePathByFullClassName(String fullClassName) {
        return javaClassesSupplier.get().get(fullClassName);
    }

}
