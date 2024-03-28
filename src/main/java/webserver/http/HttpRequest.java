package webserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static http.message.HttpMethod.Get;
import static webserver.Url.HOME_URL;
import static webserver.Url.ROOT_URL;


public class HttpRequest {
    private HttpStartLine startLine;

    private HttpHeader header;
    private byte[] body;


    private HttpRequest(BufferedReader br)throws IOException {
        this.startLine = new HttpStartLine(br.readLine());

    }

    public static HttpRequest from(BufferedReader reader)throws IOException {
        return new HttpRequest(reader);
    }

    public String getMethod(){
        return startLine.getMethod();
    }
    public String getUrl(){
        return startLine.getUrl();
    }






    }