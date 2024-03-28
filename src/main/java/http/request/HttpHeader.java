package http.request;

import java.util.Map;

// 일급 컬렉션
public class HttpHeader {
    private final Map<String, String> lines;

    private HttpHeader(Map<String, String> lines) {
        this.lines = lines;
    }

    public static HttpHeader of(Map<String, String> lines) {
        return new HttpHeader(lines);
    }

    public String getHeaderLine(String key) {
        return lines.get(key);
    }
}
