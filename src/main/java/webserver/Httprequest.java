package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Httprequest {

    private final BufferedReader br;
    private final String method;
    private final String url;
    private final String version;

    private int requestContentLength;
    private String cookie;


    public Httprequest(BufferedReader br) throws IOException {
        this.br = br;
        String startLine = this.br.readLine();
        System.out.println(startLine);
        String[] startLines = startLine.split(" ");
        this.method = startLines[0];
        this.url = startLines[1];
        this.version = startLines[2];
        while (true) {
            final String line = this.br.readLine();
            System.out.println(line);
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                this.requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if (line.startsWith("Cookie")) {
                this.cookie = line.split(": ")[1];
            }
        }
    }

    public String getMethod() {
        System.out.println(this.method);
        return method;
    }

    public String getUrl() {
        System.out.println(this.url);
        return url;
    }

    public String getCookie() {
        return cookie;
    }

    public int getRequestContentLength() {
        return requestContentLength;
    }

    public static Httprequest from(BufferedReader br) throws IOException {
        Httprequest hq = new Httprequest(br);
        return hq;
    }
}
