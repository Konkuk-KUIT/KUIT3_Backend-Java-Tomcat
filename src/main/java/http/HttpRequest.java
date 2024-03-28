package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import structure.Body;
import structure.Header;
import structure.HeaderKey;
import structure.RequestStartLine;

public class HttpRequest {

    private final RequestStartLine requestStartLine;
    private final Header header;
    private final Body body;

    public HttpRequest(BufferedReader br) throws IOException {
        this.requestStartLine = new RequestStartLine(br.readLine());
        this.header = parseHeader(br);
        this.body = parseBody(br);
    }

    private Header parseHeader(BufferedReader br) throws IOException {
        Header header = new Header();
        while (true) {
            final String line = br.readLine();
            if(line.isEmpty()) {    // Catching the empty line in HTML Message
                return header;
            }
            header.refineAttribute(line);
        }
    }

    private Body parseBody(BufferedReader br) throws IOException {
        if(hasBody()) {
            int contentLength = parseContentLength();

            char[] body = new char[contentLength];
            br.read(body, 0, contentLength);
            return new Body(String.copyValueOf(body));
        }
        return null;
    }

    private boolean hasBody() {
        return parseHeaderValue(HeaderKey.CONTENT_LENGTH) != null;  // TODO: ENUM 얘 진짜 필요없어 보이는데 parseContentLength()가 Optional로 반환한다면
    }

    private int parseContentLength() {    // 자주 써서 이 친구는 만들었습니다. TODO: 얘 따로 뺴자잉...not sure
        return Integer.parseInt(header.parseAttributeValue(HeaderKey.CONTENT_LENGTH));//TODO: hardcoidng 삭제바람...not sure
    }

    public String parseHeaderValue(HeaderKey headerKey) {
        return this.header.parseAttributeValue(headerKey);
    }

    public Map<String, String> parseBodyQueryParameter() {
        return this.body.parseQueryParameter();
    }

    public boolean isGet() {
        return this.requestStartLine.isGet();
    }

    public boolean isPost() {
        return this.requestStartLine.isPost();
    }

    public String parsePath() {
        return this.requestStartLine.parsePath();
    }

    public Map<String, String> getQueryStringMap() {
        return requestStartLine.getQueryString();
    }
}
