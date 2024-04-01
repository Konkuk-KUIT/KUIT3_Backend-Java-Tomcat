package http.request;

import http.constants.HttpMethod;
import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpRequestStartLine startLine;   // request start-line
    private final HttpHeader header;  // request header (fields)
    private final String body;  // request body
    private final Map<String, String> query;

    private HttpRequest(HttpRequestStartLine startLine, HttpHeader header, String body, Map<String, String> query) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
        this.query = query;
    }

    public static HttpRequest from(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String startLine = br.readLine();
        if (startLine == null) {
            throw new IllegalArgumentException("request is empty.");
        }

        HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);
        HttpHeader header = HttpHeader.from(br);
        String body = readBody(br, header);
        Map<String, String> query = createQuery(body);

        return new HttpRequest(httpRequestStartLine, header, body, query);
    }

    private static String readBody(BufferedReader br, HttpHeader header) throws IOException {
        if (!header.contains("Content-Length")) {
            return "";
        }

        int contentLength = Integer.parseInt(header.get("Content-Length"));
        return IOUtils.readData(br, contentLength);
    }

    private static Map<String, String> createQuery(String body) {
        // GET 방식인 경우
        if (body.isEmpty()) {
            return new HashMap<>();
        }

        // POST 방식인 경우
        return HttpRequestUtils.parseQueryParameter(body);
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getQueryParameter(String fieldName) {
        if (getMethod().equals(HttpMethod.GET.get()))
            return startLine.getQueryParameter(fieldName);
        return query.get(fieldName);
    }

    public String getField(String fieldName) {
        return header.get(fieldName);
    }
}
