package io.github.frapples.javaf4dpocket.bootstrap;

import io.github.frapples.javaf4dpocket.comm.utils.BrowserApplication;
import io.github.frapples.javaf4dpocket.comm.utils.BrowserApplication.BrowserConfig;
import io.github.frapples.javaf4dpocket.comm.utils.NetUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesktopApplication {


    public static void main(String[] args) {

        log.info("jdk版本：{}", System.getProperty("java.version"));
        int port = NetUtils.findAvailablePort(8080, 8080, 9000);
        String serverUrl = "http://127.0.0.1:" + port;
        log.info(serverUrl);
        Application.run(port);
        BrowserConfig browserConfig = new BrowserApplication.BrowserConfig()
            .setTitle("F4dpocket")
            .setIcon(new Image(DesktopApplication.class.getResourceAsStream(
                "/public/img/vue-antd-logo.png")))
            .setInitUrl(serverUrl)
            .setOnWindowClose((e) -> System.exit(0));
        BrowserApplication.run(browserConfig, args);
    }
}

