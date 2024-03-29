package webserver.http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpHeader {

    private int requestContentLength;
    private String cookie="";


    public HttpHeader(BufferedReader br) throws IOException {

        while (true) {
            String line=br.readLine();
            if (line.equals("")) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }

            if (line.startsWith("Cookie")) {
                cookie = line.split(": ")[1];
            }
        }
    }

    public int getRequestContentLength(){
        return requestContentLength;
    }
    public String getCookie(){
        return cookie;
    }
}
