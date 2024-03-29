package webserver.httpRequest;

import domain.HttpMethod;
import domain.URL;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private BufferedReader br;
    private HttpStartLine httpStartLine;
    private HttpHeader httpHeader;

    public HttpRequest(BufferedReader br) throws IOException {
        this.br = br;
        this.httpStartLine = new HttpStartLine(br.readLine());
        this.httpHeader = new HttpHeader(br);
    }

    public static HttpRequest from(BufferedReader br) throws IOException{
        return new HttpRequest(br);
    }

    public HttpMethod getMethod(){
        return httpStartLine.getMethod();
    }

    public URL getUrl(){
        return httpStartLine.getPath();
    }

    public int getContentLength(){
        return httpHeader.getContentLength();
    }

    public String getCookie(){
        return httpHeader.getCookie();
    }


}
