package HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestMessage {
    private RequestStartLine startLine;
    private RequestHeader header;
    private String body;

    public RequestMessage(RequestStartLine startLine, RequestHeader header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static RequestMessage from(BufferedReader br) throws IOException {

        // StartLine 파싱
        RequestStartLine startLine = RequestStartLine.from(br.readLine());
        // Header 파싱
        RequestHeader header = RequestHeader.from(br);
        // Body 파싱
        StringBuilder bodyBuilder = new StringBuilder();
        while (br.ready()) {
            bodyBuilder.append((char) br.read());
        }
        String body = bodyBuilder.toString().trim();

        return new RequestMessage(startLine, header, body);
    }

    public RequestStartLine getStartLine() {
        return this.startLine;
    }

    public RequestHeader getHeader() {
        return this.header;
    }

//    public RequestHeader getHeader() {
//        return this.header.getHeader();
//    }

    // 메인에서 객체 만들 때 일케 만듦
//    RequestMessage requestMessage = RequestMessage.from(BufferedReader);
}
