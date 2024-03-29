package http;

import java.io.FileInputStream;
import java.io.IOException;
import http.structure.Header;
import http.structure.HeaderKey;
import http.structure.ResponseStartLine;

public class HttpResponse {
    private final byte[] startLine;
    private final byte[] header;
    private final byte[] body;

    private HttpResponse(byte[] startLine, byte[] header, byte[] body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpResponse ofFile(ResponseStartLine startLine, Header header, String filePath) {
        byte[] body = readFile(filePath);
        byte[] byteStartLine = startLine.toByte();
        header.addAttribute(HeaderKey.CONTENT_LENGTH, Integer.toString(body.length));
        byte[] byteHeader = header.getFinalByteHeader();

        return new HttpResponse(byteStartLine, byteHeader, body);
    }

    public static HttpResponse ofPath(ResponseStartLine startLine, Header header, String path) {
        byte[] body = new byte[0];
        byte[] byteStartLine = startLine.toByte();
        header.addAttribute(HeaderKey.LOCATION, path);
        byte[] byteHeader = header.getFinalByteHeader();

        return new HttpResponse(byteStartLine, byteHeader, body);
    }

    public byte[] getHeader() {
        return this.header;
    }

    public byte[] getBody() {
        return this.body;
    }

    public byte[] getStartLine() {
        return this.startLine;
    }

    public int getBodyLength() {
        return this.body.length;
    }

    private static byte[] readFile(String path) {
        try (FileInputStream input = new FileInputStream(path)) {
            return input.readAllBytes();
        } catch (IOException e) {
            System.out.println("잘못된 경로입니다.");
            throw new RuntimeException(e);
        }
    }
}
