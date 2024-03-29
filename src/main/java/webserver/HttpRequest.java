package webserver;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRequest {

    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final String HttpStartLine;
    private final String method;
    private final String url;
    private final String HttpHeader;
    private String body = null;
    private int requestContentLength = 0;
    private String cookieString;

    private HttpRequest(BufferedReader br) throws IOException {
        HttpStartLine = br.readLine();
        String[] startLines = HttpStartLine.split(" ");
        method = startLines[0]; //GET POST ...
        url = startLines[1]; ///login.html /index.html /user/form.html...
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if(line.startsWith("Cookie: ")){
                cookieString = line;
            }
            stringBuilder.append(line).append("\n");
        }
        if(method.equals("POST")){
            body = IOUtils.readData(br, requestContentLength);
        }
        HttpHeader = stringBuilder.toString();
        log.log(Level.INFO, HttpHeader);
        log.log(Level.INFO, body);
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }
    public boolean cookieLogined(){
        final String loginFlag = cookieString.substring(cookieString.lastIndexOf("=")+1);
        if(loginFlag.equals("true"))
            return true;
        return false;
    }
    //Getter
    public String getMethod() {
        return method;
    }
    public String getUrl() {
        return url;
    }
    public String getHttpHeader() {
        return HttpHeader;
    }
    public String getMessagebody() {
        return body;
    }
}