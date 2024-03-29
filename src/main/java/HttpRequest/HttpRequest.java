package HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;

import static http.util.IOUtils.readData;

public class HttpRequest {
    private MessageStartLine startLine;
    private MessageHeader header;
    private String body;

    public HttpRequest(MessageStartLine startLine, MessageHeader header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {

        // StartLine 파싱
        MessageStartLine startLine = MessageStartLine.from(br.readLine());
        // Header 파싱
        MessageHeader header = MessageHeader.from(br);
        // Body 파싱
        String body = readData(br, header.getContentLength());

        return new HttpRequest(startLine, header, body);
    }

    public String getMethod() {
        return this.startLine.getMethod();
    }

    public String getUrl() {
        return this.startLine.getUrl();
    }

    public int getContentLength() {
        return this.header.getContentLength();
    }

    public String getCookie() {
        return this.header.getCookie();
    }

    public String getBody() {
        return this.body;
    }
}
