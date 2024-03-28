package http.request;

import http.util.HttpRequestUtils;

import java.util.Map;

public class RequestBody {
    private Map<String, String> parameters;

    private RequestBody(String body) {
        if (!body.isEmpty()) {
            parameters = HttpRequestUtils.parseQueryParameter(body);
        }
    }

    public static RequestBody of(String body) {
        return new RequestBody(body);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
