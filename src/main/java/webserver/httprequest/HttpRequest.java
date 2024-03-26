package webserver.httprequest;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    // httpStartLine
    // header -> 클래스로 분리
    // body -> 클래스로 분리

    private BufferedReader br;
    private HttpRequestLine httpRequestLine;

    public BufferedReader getBr() {
        return br;
    }

    private HttpRequestHeaderLines httpRequestHeaderLines;

    public HttpRequest(BufferedReader br) throws IOException {
        this.br = br;
        this.httpRequestLine = new HttpRequestLine(br.readLine());
        this.httpRequestHeaderLines = new HttpRequestHeaderLines(br);
    }

    public static HttpRequest from(BufferedReader br) throws IOException{
        return new HttpRequest(br);
    }

    public String getMethod(){
        return httpRequestLine.getMethod();
    }

    public String getUrl(){
        return httpRequestLine.getPath();
    }

    public int getContentLength(){
        return httpRequestHeaderLines.getContentLength();
    }

    public String getCookie(){
        return httpRequestHeaderLines.getCookie();
    }



}
