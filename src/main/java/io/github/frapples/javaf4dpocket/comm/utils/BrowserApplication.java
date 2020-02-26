package io.github.frapples.javaf4dpocket.comm.utils;

import com.sun.javafx.webkit.WebConsoleListener;
import io.github.frapples.javaf4dpocket.comm.utils.BrowserApplication.BrowserConfig;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/5/17
 */
@Slf4j
public class BrowserApplication extends Application {

    private MyBrowser myBrowser;

    @Data
    @Accessors(chain = true)
    public static class BrowserConfig {

        /**
         * 初始化路径，默认
         */
        private String initUrl;
        private String title = "Application";
        private Image icon;
        private boolean resizable = true;
        private Double width;
        private Double height;
        private double minWidth = 0;
        private double minHeight = 0;
        private double maxWidth = Double.MAX_VALUE;
        private double maxHeight = Double.MAX_VALUE;

        /**
         * 窗口退出的回调
         */
        private EventHandler<WindowEvent> onWindowClose;

        /**
         * https://stackoverflow.com/questions/8341305/how-to-remove-javafx-stage-buttons-minimize-maximize-close
         */
        private StageStyle stageStyle;
    }

    @Setter
    private BrowserConfig browserConfig;


    public BrowserApplication() {
    }

    public static double getScreenWidth() {
        // https://docs.oracle.com/javafx/2/api/javafx/stage/Screen.html
        Rectangle2D v = Screen.getPrimary().getVisualBounds();
        return v.getWidth();
    }

    public static double geScreenHeight() {
        Rectangle2D v = Screen.getPrimary().getVisualBounds();
        return v.getHeight();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(browserConfig.getWidth() == null ? BrowserApplication.getScreenWidth() * 0.8 : browserConfig.getWidth());
        primaryStage.setHeight(browserConfig.getHeight() == null ? BrowserApplication.geScreenHeight() * 0.8 : browserConfig.getHeight());
        primaryStage.setMinWidth(browserConfig.getMinWidth());
        primaryStage.setMinHeight(browserConfig.getMinHeight());
        primaryStage.setMaxHeight(browserConfig.getMaxHeight());
        primaryStage.setMaxWidth(browserConfig.getMaxWidth());
        primaryStage.setResizable(browserConfig.isResizable());
        primaryStage.setTitle(browserConfig.getTitle());
        if (browserConfig.getIcon() != null) {
            primaryStage.getIcons().add(browserConfig.getIcon());
        }

        if (browserConfig.getStageStyle() != null) {
            primaryStage.initStyle(browserConfig.getStageStyle());
        }

        if (browserConfig.getOnWindowClose() != null) {
            primaryStage.setOnCloseRequest(browserConfig.getOnWindowClose());
        }

        this.myBrowser = new MyBrowser(browserConfig);
        Scene scene = new Scene(myBrowser);

        // 第三方样式库
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void load() {
        this.myBrowser.load();
    }

}

@Slf4j
class MyBrowser extends StackPane {

    static {
        // 要注意的是，Listener如果有别人调用这个函数，将会被覆盖
        WebConsoleListener.setDefaultListener(MyBrowser::onConsolePrint);
    }

    private final BrowserConfig browserConfig;
    private final WebView webView;

    private static void onConsolePrint(WebView webView, String message, int lineNumber, String sourceId) {
        log.info("Browser Console: [" + sourceId + ":" + lineNumber + "] " + message);
    }

    MyBrowser(BrowserConfig browserConfig) {
        this.browserConfig = browserConfig;
        this.webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webView.addEventHandler(WebEvent.ANY, e -> {
            log.error("{}", e.getData());
        });

        webEngine.setOnError(event -> log.error(event.getMessage()));
        webEngine.getLoadWorker().exceptionProperty().addListener(
            (ov, t, t1) -> log.info("Received exception: {}, {}", t, t1));


        webView.setContextMenuEnabled(true);
        getChildren().add(webView);
    }

    void load() {
        log.info("加载url：" + browserConfig.getInitUrl());
        webView.getEngine().load(this.browserConfig.getInitUrl());
    }

}
