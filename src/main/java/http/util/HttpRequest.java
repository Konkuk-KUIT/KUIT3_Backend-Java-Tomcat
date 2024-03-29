package http.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    // private final String body;

    private HttpRequest(String method, String path, String version, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        String[] startLineTokens = startLine.split(" ");
        String method = startLineTokens[0];
        String path = startLineTokens[1];
        String version = startLineTokens[2];

        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ");
            headers.put(headerParts[0], headerParts[1]);
        }

        return new HttpRequest(method, path, version, headers);
    }

    // Getter 메서드들
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

//    public String getBody() {
//        return body;
//    }

    // 특정 헤더 값을 가져오는 메서드 추가
    public String getHeader(String name) {
        return headers.getOrDefault(name, null);
    }
}
