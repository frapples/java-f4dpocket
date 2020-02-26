package io.github.frapples.javaf4dpocket.bootstrap;

import io.github.frapples.javaf4dpocket.comm.utils.PathUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/5/18
 */

@Slf4j
public class SimpleRemoteLibraryCacheService {

    private String cacheDirectory;

    public SimpleRemoteLibraryCacheService(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }


    @Data
    @Accessors(chain = true)
    public static class CDNResource {
        InputStream inputStream;
        long lastModified;
        String contentType;
    }

    @SneakyThrows
    public CDNResource get(String url) {
        String libName = FilenameUtils.getName(url);
        File file = new File(PathUtils.concat(cacheDirectory, libName));
        String contentTypeFileName = PathUtils.concat(cacheDirectory, libName + ".cache");

        if (!file.exists()) {
            File dir = new File(cacheDirectory);
            if (!dir.exists()) {
                dir.mkdir();
            }

            log.info("下载：" + url);
            HttpResponse response = Request.Get(url).connectTimeout(5000).socketTimeout(5000)
                .execute().returnResponse();
            log.info("下载完成：" + url);
            String contentType = response.getEntity().getContentType().getValue();
            File contentTypeFile = new File(contentTypeFileName);
            if (!contentTypeFile.exists()) {
                contentTypeFile.createNewFile();
            }

            FileUtils.writeStringToFile(contentTypeFile, contentType, "UTF-8");

            file.createNewFile();
            IOUtils.copy(response.getEntity().getContent(), new FileOutputStream(file));
        } else {
            log.info("命中缓存:" + url);
        }

        String contentType = FileUtils.readFileToString(new File(contentTypeFileName), "UTF-8");
        return new CDNResource()
            .setContentType(contentType)
            .setInputStream(new FileInputStream(file))
            .setLastModified(file.lastModified());
    }
}
