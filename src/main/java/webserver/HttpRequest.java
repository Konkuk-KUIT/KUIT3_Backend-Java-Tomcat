package webserver;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private String method;
    private String path;
    private int contentLength = 0;
    private String cookie = "";
    private String body;

    private HttpRequest() {
    }

    // 정적 팩토리 메서드
    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequest request = new HttpRequest();
        String startLine = br.readLine();
        String[] requestLine = startLine.split(" ");
        request.method = requestLine[0];
        request.path = requestLine[1];


        while (true) {
            String line = br.readLine();
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                request.contentLength = Integer.parseInt(line.split(": ")[1]);

            }
            if (line.startsWith("Cookie")) {
                request.cookie = line.split(": ")[1];
            }
        }
        if (request.contentLength > 0) {
            request.body = IOUtils.readData(br, request.getContentLength());
        }
        return request;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getCookie() {
        return cookie;
    }

    public String getBody(){
        return body;
    }

}