package io.github.frapples.javaf4dpocket.bootstrap;

import io.github.frapples.javaf4dpocket.comm.utils.NetUtils;
import io.github.frapples.javaf4dpocket.comm.utils.javafx.BrowserApplication;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesktopApplication extends BrowserApplication {

    private static int port;

    public DesktopApplication() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                initConcurrently();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                onInitConcurrentlyDone();
            }
        };
        new Thread(task).start();

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

    private void initConcurrently() {
        log.info("Server启动");
        Application.runAndAwaitInit(DesktopApplication.port);
        log.info("Server启动完成");
    }

    private void onInitConcurrentlyDone() {
        if (!inited && primaryStage != null) {
            this.load();
            primaryStage.show();
            inited = true;
        }
    }

    private volatile Stage primaryStage;
    private volatile boolean inited;

    @Override
    @SneakyThrows
    public void start(Stage primaryStage) {
        super.start(primaryStage);
        this.primaryStage = primaryStage;
        log.info("GUI启动完成");
        onInitConcurrentlyDone();
    }


    public static void main(String[] args) {
        log.info("jdk版本：{}", System.getProperty("java.version"));
        Application.ApplicationConfig config = Application.getBean(Application.ApplicationConfig.class);;
        DesktopApplication.port = NetUtils.findAvailablePort(config.getPort(), 8080, 9000);
        log.info("GUI启动");
        BrowserApplication.launch(DesktopApplication.class, args);
    }
}

