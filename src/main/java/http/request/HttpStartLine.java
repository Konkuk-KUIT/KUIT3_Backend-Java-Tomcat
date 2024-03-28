package http.request;

import http.util.HttpRequestUtils;

import java.util.Map;

public class HttpStartLine {
    private final String method;
    private final String path;
    private final String version;
    private Map<String, String> parameters;

    private HttpStartLine(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpStartLine of(String method, String path, String version) {
        return new HttpStartLine(method, path, version);
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

    public void initParameters(String queryString) {
        parameters = HttpRequestUtils.parseQueryParameter(queryString);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
