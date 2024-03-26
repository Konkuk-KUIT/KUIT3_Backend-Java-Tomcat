package webserver.httprequest;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;

// Entity Body
public class HttpRequestBody {
    Map<String,String> body = new HashMap<>();

    public HttpRequestBody(BufferedReader br, int contentLength) throws IOException {
        String queryString = IOUtils.readData(br, contentLength);
        body = parseQueryParameter(queryString);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
