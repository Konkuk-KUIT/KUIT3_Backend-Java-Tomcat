package HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageHeader {
    private int contentLength;
    private String cookie;

    public MessageHeader(int contentLength, String cookie) {
        this.contentLength = contentLength;
        this.cookie = cookie;
    }

    public static MessageHeader from(BufferedReader br) throws IOException {
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
                System.out.println(contentLength);
            }
            if (line.startsWith("Cookie")) {
                cookie = line;
            }
        }
        return new MessageHeader(contentLength, cookie);
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getCookie() {
        return this.cookie;
    }
}
