package http.request;

import http.util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLine {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> query;

    public HttpRequestStartLine(String method, String path, String version, Map<String, String> query) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.query = query;
    }

    public static HttpRequestStartLine from(String startLine) {
        String[] elements = startLine.split(" ");

        String method = elements[0];
        String path = elements[1];
        String version = elements[2];
        Map<String, String> query = createQuery(path);

        return new HttpRequestStartLine(method, path, version, query);
    }

    private static Map<String, String> createQuery(String path) {
        // POST 방식인 경우
        int index = path.indexOf("?");
        if (index == -1) {
            return new HashMap<>();
        }

        // GET 방식인 경우
        String queryString = path.substring(index + 1);
        return HttpRequestUtils.parseQueryParameter(queryString);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getQueryParameter(String queryParameter) {
        return query.get(queryParameter);
    }
}
