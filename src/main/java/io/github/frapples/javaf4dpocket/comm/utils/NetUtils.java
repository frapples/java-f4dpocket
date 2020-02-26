package io.github.frapples.javaf4dpocket.comm.utils;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetUtils {


    public static int findAvailablePort(int defaultPort) {
        return findAvailablePort(defaultPort, 0, 65535);
    }

    public static int findAvailablePort(int defaultPort, int minPort, int maxPort) {
        Preconditions.checkArgument(defaultPort >= minPort && defaultPort < maxPort);
        for (int port = defaultPort; port < maxPort; port++) {
            if (!isPortUsed(port)) {
                return port;
            }
        }

        for (int port = defaultPort - 1; port > minPort; port--) {
            if (!isPortUsed(port)) {
                return port;
            }
        }
        throw new NoSuchElementException("no available port.");
    }

    public static boolean isPortUsed(int port) {
        boolean used;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            used = false;
        } catch (IOException e) {
            used = true;
        }
        return used;
    }

}
