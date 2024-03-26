package http;

import java.io.FileInputStream;
import java.io.IOException;

public class HttpResponse {
    private final byte[] header;
    private final byte[] body;

    private HttpResponse(byte[] header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public static HttpResponse ofHtmlFile(String path) {
        byte[] body = readFile(path);
        byte[] header = getHttpResponseHeader(body.length);
        return new HttpResponse(header, body);
    }

    public byte[] getHeader() {
        return this.header;
    }

    public byte[] getBody() {
        return this.body;
    }

    public int getBodyLength() {
        return this.body.length;
    }

    private static byte[] readFile(String path) {
        try (FileInputStream input = new FileInputStream(path)) {    // TODO: 얘 고쳐라, Path.of() 몰라
            return input.readAllBytes();
        } catch (IOException e) {
            System.out.println("잘못된 경로입니다.");
            throw new RuntimeException(e);
        }
    }

    private static byte[] getHttpResponseHeader(int contentLength) {    // html 파일일경우
        String header = "HTTP/1.1 200 OK \r\n" + "Content-Type: text/html;charset=utf-8\r\n" + "Content-Length: " +
                contentLength + "\r\n" + "\r\n";
        return header.getBytes();
    }
}
