package http.request;

import http.util.IOUtils;
import util.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final BufferedReader br;
    private HttpStartLine httpStartLine;
    private HttpHeader httpHeader;
    private RequestBody requestBody;

    private HttpRequest(InputStream in) {
        this.br = new BufferedReader(new InputStreamReader(in));
        init();
    }

    private void init() {
        try {
            createHttpStartLine();
            createHttpHeader();
            createRequestBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createRequestBody() throws IOException {
        String body = "";
        if (getMethod().equals(HttpMethod.POST.value())) {
            int requestContentLength = Integer.parseInt(getHeaderLine("Content-Length"));
            body = IOUtils.readData(br, requestContentLength);
        }
        requestBody = RequestBody.of(body);
    }

    private void createHttpHeader() throws IOException {
        Map<String, String> lines = new HashMap<>();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] elements = line.split(": ");
            String key = elements[0];
            String value = elements[1];
            lines.put(key, value);
        }

        httpHeader = HttpHeader.of(lines);
    }

    private void createHttpStartLine() throws IOException {
        String startLine = br.readLine();
        String[] elements = startLine.split(" ");
        String method = elements[0];
        String url = elements[1];
        String version = elements[2];
        httpStartLine = HttpStartLine.of(method, url, version);

        if (getMethod().equals(HttpMethod.GET.value())) {
            int index = getPath().indexOf("?");
            String queryString = getPath().substring(index + 1);
            httpStartLine.initParameters(queryString);
        }
    }

    public static HttpRequest from(InputStream in) {
        return new HttpRequest(in);
    }

    public String getMethod() {
        return httpStartLine.getMethod();
    }

    public String getPath() {
        return httpStartLine.getPath();
    }

    public String getVersion() {
        return httpStartLine.getVersion();
    }

    public String getHeaderLine(String key) {
        return httpHeader.getHeaderLine(key);
    }

    public String getParameter(String key) throws IOException {
        if (getMethod().equals(HttpMethod.GET.value()))
            return httpStartLine.getParameter(key);
        return requestBody.getParameter(key);
    }
}
