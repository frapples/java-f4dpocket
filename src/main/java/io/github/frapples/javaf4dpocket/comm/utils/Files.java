package io.github.frapples.javaf4dpocket.comm.utils;

import java.io.File;
import java.util.Iterator;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/12
 */
public class Files {

    public static Iterator<File> iterateFiles(final File directory, Predicate<File> fileFilter, Predicate<File> dirFilter) {
        return FileUtils.iterateFiles(directory, new PredicateFileFilter(fileFilter), new PredicateFileFilter(dirFilter));
    }

    @AllArgsConstructor
    public static class PredicateFileFilter extends AbstractFileFilter {

        private final Predicate<File> predicate;

        @Override
        public boolean accept(File file) {
            return predicate.test(file);
        }
    }
}
