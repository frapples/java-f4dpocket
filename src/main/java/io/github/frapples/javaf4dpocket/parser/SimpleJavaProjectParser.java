package io.github.frapples.javaf4dpocket.parser;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/5/22
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleJavaProjectParser {

    private String root;

    @Getter
    private Set<String> javaClassNames;


    public static SimpleJavaProjectParser of(String root) {
        SimpleJavaProjectParser parser = new SimpleJavaProjectParser();
        parser.root = root;
        return parser;
    }

    @SneakyThrows
    public void parse() {
        Collection<File> allJavaFiles = FileUtils.listFiles(new File(this.root), new String[]{"java"}, true);
        Pattern pattern = Pattern.compile("package\\s+([^;]+);");
        TreeSet<String> javaClassNames = new TreeSet<>();
        for (File javaFile : allJavaFiles) {
            String javaClass = FilenameUtils.getBaseName(javaFile.getName());
            Files.asCharSource(javaFile, StandardCharsets.UTF_8).forEachLine((line) -> {
                Matcher m = pattern.matcher(line.trim());
                if (m.matches()) {
                    javaClassNames.add(m.group(1) + "." + javaClass);
                }
            });
        }
        this.javaClassNames = javaClassNames;
    }

    public Set<String> getPackageNames() {
        return getJavaClassNames().stream().map(fullName -> {
            return Iterables.getLast(Splitter.on(".").split(fullName));
        }).collect(Collectors.toCollection(TreeSet::new));
    }

}
