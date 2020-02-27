package io.github.frapples.javaf4dpocket.comm.utils.javafx;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.event.EventListenerSupport;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2020/2/27
 */
@Slf4j
public class JavaFxUtils {

    @Getter
    private static EventListenerSupport<WebConsoleListener> WebConsoleLogEvent = new EventListenerSupport<>(WebConsoleListener.class);

    static {
        // 这个Listener只允许有一个，这里利用事件分发机制来支持多个监听者
        WebConsoleListener.setDefaultListener(JavaFxUtils::onConsolePrint);
    }


    private static void onConsolePrint(WebView webView, String message, int lineNumber, String sourceId) {
        WebConsoleLogEvent.fire().messageAdded(webView, message, lineNumber, sourceId);
    }
}
