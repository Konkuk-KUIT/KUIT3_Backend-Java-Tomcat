package structure;

import http.util.HttpRequestUtils;
import java.util.HashMap;
import java.util.Map;

public class RequestStartLine {
    private final HttpMethod httpMethod;
    private final String path;

    private final Map<String, String> queryString;
    private final String version;

    public RequestStartLine(String line) {
        String[] startLines = line.split(" ");  // delimeter없으면 있는 그대로 반환
        this.httpMethod = parseHttpMethod(startLines[0]);
        this.path = parsePath(startLines[1]);
        this.queryString = parseQueryString(startLines[1]);
        this.version = startLines[2];
    }

    private HttpMethod parseHttpMethod(String startLine) {
        String[] startLines = startLine.split(" ");
        try {
            return HttpMethod.valueOf(startLines[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("옳지 않은 HTTP METHOD");
        }
        return null;
    }

    private String parsePath(String line) {
        return line.split("\\?")[0];
    }

    private Map<String, String> parseQueryString(String line) {
        String queryString;
        try {
            queryString = line.split("\\?")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return new HashMap<>();
        }

        return parseQueryParameter(queryString);
    }

    private Map<String, String> parseQueryParameter (String queryString) {
        return HttpRequestUtils.parseQueryParameter(queryString);
    }

    public String parsePath() {
        return this.path;
    }

    public boolean isPost() {
        return this.httpMethod == HttpMethod.POST;
    }

    public boolean isGet() {
        return this.httpMethod == HttpMethod.GET;
    }
}
