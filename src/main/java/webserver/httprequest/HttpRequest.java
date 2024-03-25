package webserver.httprequest;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    // httpStartLine
    // header -> 클래스로 분리
    // body -> 클래스로 분리

    private BufferedReader br;
    private HttpRequestFirstLine httpRequestFirstLine;

    public BufferedReader getBr() {
        return br;
    }

    private HttpRequestHeader httpRequestHeader;

    public HttpRequest(BufferedReader br) throws IOException {
        this.br = br;
        this.httpRequestFirstLine = new HttpRequestFirstLine(br.readLine());
        this.httpRequestHeader = new HttpRequestHeader(br);
    }

    public static HttpRequest from(BufferedReader br) throws IOException{
        return new HttpRequest(br);
    }

    public String getMethod(){
        return httpRequestFirstLine.getMethod();
    }

    public String getUrl(){
        return httpRequestFirstLine.getPath();
    }

    public int getContentLength(){
        return httpRequestHeader.getContentLength();
    }

    public String getCookie(){
        return httpRequestHeader.getCookie();
    }



}
