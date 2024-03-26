package webserver.httprequest;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestHeaderLines {
    private int contentLength;
    private String cookie;

    public int getContentLength() {
        return contentLength;
    }

    public String getCookie() {
        return cookie;
    }

    public HttpRequestHeaderLines(BufferedReader br) throws IOException {
        while (true) {
            final String line = br.readLine();
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if (line.startsWith("Cookie")) {
                cookie = line.split(": ")[1];
                cookie = cookie.split(" ")[0];
            }
        }
    }
}
