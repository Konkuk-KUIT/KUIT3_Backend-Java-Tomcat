package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {
    private final Map<String, String> header; // header fields

    public HttpHeader(Map<String, String> header) {
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

    public void put(String name, String value) {
        header.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String name : header.keySet()) {
            sb.append(name)
                    .append(": ")
                    .append(header.get(name))
                    .append("\r\n");
        }

        return sb.append("\r\n").toString();
    }
}
