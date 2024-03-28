package HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestHeader {
    private int contentLength;
    private String cookie;

    public RequestHeader(int contentLength, String cookie) {
        this.contentLength = contentLength;
        this.cookie = cookie;
    }

    public static RequestHeader from(BufferedReader br) throws IOException {
        int contentLength = 0;
        String cookie = "";

        while (true) {
            final String line = br.readLine();
            // blank line 만나면 requestBody 시작되므로 break
            if (line.equals("")) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if (line.startsWith("Cookie")) {
                cookie = line;
            }
        }
        return new RequestHeader(contentLength, cookie);
    }

    public void setHeader(RequestHeader header) {
        this.contentLength = header.contentLength;
        this.cookie = header.cookie;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getCookie() {
        return this.cookie;
    }
}
