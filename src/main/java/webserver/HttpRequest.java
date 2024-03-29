package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpRequest {

    private final String method;
    private final String url;
    private String cookie;
    private byte[] body = new byte[0];
    private String queryString;


    public HttpRequest(String method, String url, String cookie, String queryString) {
        this.method = method;
        this.url = url;
        this.cookie = cookie;
        this.queryString = queryString;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {

        // Start Line - method, url
        String line = br.readLine();
        String[] startLines = line.split(" ");

        int requestContentLength = 0;
        String cookie = "";

        // Header - Content-Length, Cookie
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1].trim());
            }
            if (line.startsWith("Cookie:")) {
                cookie = line.split(": ")[1].trim();
            }
        }

        // QueryString(body)
        StringBuilder stringBuilder = new StringBuilder();
        if (requestContentLength > 0) {
            for (int i = 0; i < requestContentLength; i++) {
                stringBuilder.append((char) br.read());
            }
        }
        String queryString = stringBuilder.toString();
        return new HttpRequest((startLines[0]), startLines[1], cookie, queryString);
    }


    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getCookie() {
        return cookie;
    }

    public String getQueryString() {
        return queryString;
    }
}
