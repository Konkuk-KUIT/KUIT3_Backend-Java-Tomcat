package http;

import java.io.FileInputStream;
import java.io.IOException;
import structure.Header;
import structure.HeaderKey;
import structure.ResponseStartLine;

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

    public static HttpResponse of200HtmlFile(String path) {
        byte[] body = readFile(path);
        byte[] header = get200HtmlResponseHeader(body.length);
        return new HttpResponse(header, body, null);
    }

    public static HttpResponse of302ResponseHeader(String path) {
        byte[] header = get302ResponseHeader(path);
        return new HttpResponse(header, new byte[0], null);
    }

    public static HttpResponse of200CssResponse(String path) {
        byte[] body = readFile(path);
        byte[] header = get200CssResponseHeader(body.length);
        return new HttpResponse(header, body, null);
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
        try (FileInputStream input = new FileInputStream(path)) {    // TODO: 얘 고쳐라, Path.of() 몰라
            return input.readAllBytes();
        } catch (IOException e) {
            System.out.println("잘못된 경로입니다.");
            throw new RuntimeException(e);
        }
    }

    private static byte[] get200HtmlResponseHeader(int contentLength) {    // html 파일일경우
        String header = "HTTP/1.1 200 OK \r\n" + "Content-Type: text/html;charset=utf-8\r\n" + "Content-Length: " +
                contentLength + "\r\n" + "\r\n";
        return header.getBytes();
    }

    private static byte[] get302ResponseHeader(String path) {
        String header = "HTTP/1.1 302 OK \r\n" + "Location: " + path + "\r\n"
                + "\r\n";
        return header.getBytes();
    }

    private static byte[] get200CssResponseHeader(int contentLength) {
        String header = "HTTP/1.1 200 OK \r\n" + "Content-Type: text/css\r\n" + "Content-Length: " +
                contentLength + "\r\n" + "\r\n";
        return header.getBytes();
    }
}
