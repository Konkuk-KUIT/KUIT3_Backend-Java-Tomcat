package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {
    private final Map<String, String> header; // header fields

    private HttpHeader(Map<String, String> header) {
        this.header = header;
    }

    public static HttpHeader from(BufferedReader br) throws IOException {
        return new HttpHeader(readHeader(br));
    }

    private static Map<String, String> readHeader(BufferedReader br) throws IOException {
        Map<String, String> header = new HashMap<>();
        String field = "";

        while (!(field = br.readLine()).isEmpty()) {
            String[] pair = field.split(": ");
            String name = pair[0];
            String value = pair[1];
            header.put(name, value);
        }

        return header;
    }

    public boolean contains(String fieldName) {
        return header.containsKey(fieldName);
    }

    public String get(String fieldName) {
        return header.get(fieldName);
    }
}
