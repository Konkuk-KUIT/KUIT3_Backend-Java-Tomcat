package http.util.request;

import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.IOUtils.readData;

public class HttpRequest {

    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private BufferedReader br;
    private String method;
    private String url;

    private int requestContentLength = 0;
    private boolean loginCookie = false;


    private HttpRequest(BufferedReader br) throws IOException {
        this.br = br;
        String startLine = br.readLine();
        String[] startLines = startLine.split(" ");
        method = startLines[0];
        url = startLines[1];


        log.log(Level.INFO, startLine + "\n");




        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
            // cookie info
            if (line.startsWith("Cookie: logined=true")) {
                log.log(Level.INFO, "test1\n");
                loginCookie = true;
            }
        }
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl(){
        return url;
    }

    public String getBody() throws IOException {
        return readData(br, requestContentLength);
    }


    public boolean isLoginCookie() {
        return loginCookie;
    }
}
