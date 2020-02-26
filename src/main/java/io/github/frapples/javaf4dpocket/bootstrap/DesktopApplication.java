package io.github.frapples.javaf4dpocket.bootstrap;

import io.github.frapples.javaf4dpocket.comm.utils.BrowserApplication;
import io.github.frapples.javaf4dpocket.comm.utils.NetUtils;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesktopApplication extends BrowserApplication {


    private static int port;

    public DesktopApplication() {
        String serverUrl = "http://127.0.0.1:" + port;
        log.info(serverUrl);
        BrowserConfig browserConfig = new BrowserConfig()
            .setTitle("F4dpocket")
            .setIcon(new Image(DesktopApplication.class.getResourceAsStream(
                "/public/img/vue-antd-logo.png")))
            .setInitUrl(serverUrl)
            .setOnWindowClose((e) -> {
                Application.stop();
                System.exit(0);
            });
        this.setBrowserConfig(browserConfig);
    }


    @Override
    public void start(Stage primaryStage) {
        super.start(primaryStage);
        this.load();
        log.info("GUI启动完成");
    }

    public static void main(String[] args) {
        log.info("jdk版本：{}", System.getProperty("java.version"));
        DesktopApplication.port = NetUtils.findAvailablePort(8080, 8080, 9000);
        log.info("Server启动");
        Application.runAndAwaitInit(DesktopApplication.port);
        log.info("Server启动完成");
        log.info("GUI启动");
        BrowserApplication.launch(DesktopApplication.class, args);
    }
}

