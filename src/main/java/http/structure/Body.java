package http.structure;

import http.util.HttpRequestUtils;
import java.util.Map;

public class Body {
    private final String body;

    public Body(String body) {
        this.body = body;
    }

    public Map<String, String> parseQueryParameter() {
        return HttpRequestUtils.parseQueryParameter(this.body);
    }
}
