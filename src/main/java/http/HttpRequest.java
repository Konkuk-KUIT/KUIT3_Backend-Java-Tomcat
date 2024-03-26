package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import structure.Body;
import structure.Header;
import structure.StartLine;

public class HttpRequest {
    private final StartLine startLine;
    private final Header header;
    private final Body body;

    public HttpRequest(BufferedReader br) throws IOException {
        this.startLine = parseStartLine(br);
        System.out.println(startLine);
        this.header = parseHeader(br);
        this.body = parseBody(br);
    }

    private StartLine parseStartLine(BufferedReader br) throws IOException {    // TODO: parser 딴데서도 쓰이니까 따로 뺴 아닌데
        return new StartLine(br.readLine());
    }

    private Header parseHeader(BufferedReader br) throws IOException {
        Header header = new Header();
        while (true) {
            final String line = br.readLine();
            if(line.isEmpty()) {    // Catching the empty line in HTML Message
                return header;
            }
            header.addAttribute(line);
        }
    }

    private Body parseBody(BufferedReader br) throws IOException {
        int contentLength = parseContentLength();

        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return new Body(String.copyValueOf(body));
    }

    public int parseContentLength() {    // 자주 써서 이 친구는 만들었습니다. TODO: 얘 따로 뺴자잉...not sure
        return Integer.parseInt(header.parseAttributeValue("Content-Length"));//TODO: hardcoidng 삭제바람...not sure
    }

    public String parseHeaderValue(String key) {
        return this.header.parseAttributeValue(key);
    }

    public Map<String, String> parseBodyQueryParameter() {
        return this.body.parseQueryParameter();
    }

    public boolean isGet() {
        return this.startLine.isGet();
    }

    public boolean isPost() {
        return this.startLine.isPost();
    }

    public String parsePath() {
        return startLine.getPath();
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "startLine=" + startLine +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}
