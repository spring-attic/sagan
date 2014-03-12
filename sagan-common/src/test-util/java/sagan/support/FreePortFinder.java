package sagan.support;

import java.io.IOException;
import java.net.Socket;

public class FreePortFinder {

    public static int find() {
        for (int p = 8080; p < 9000; p++) {
            if (isPortAvailable(p)) {
                return p;
            }
        }
        throw new RuntimeException("unable to find any available ports");
    }

    private static boolean isPortAvailable(int port) {
        Socket s = null;
        try {
            s = new Socket("localhost", port);
            return false;
        } catch (IOException e) {
            return true;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close socket to port " + port, e);
                }
            }
        }
    }
}
