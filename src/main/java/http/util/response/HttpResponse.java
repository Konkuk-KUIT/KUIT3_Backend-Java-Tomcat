package http.util.response;

import http.util.request.HttpHeaderList;
import http.util.request.UrlList;
import webserver.RequestHandler;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpResponse {

    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private DataOutputStream dos;


    private UrlList url;

    private HttpResponse(DataOutputStream dos){
        this.dos = dos;

    }

    public static HttpResponse from(DataOutputStream dos){
        return new HttpResponse(dos);
    }




    public void getForward(String url) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get(url));
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    public void getForwardWithCss(String url) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get(url));
        response200HeaderCss(dos, body.length);
        responseBody(dos, body);
    }

    public void getRedirect(String url) {
        response302Header(dos, url);
    }

    public void getRedirectWithCookie(String url) {
        response302HeaderWithCookie(dos, url);
    }







    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HttpHeaderList.HTTP_1_1 + "200 OK \r\n");
            dos.writeBytes(HttpHeaderList.CONTENT_TYPE +  "text/html;charset=utf-8\r\n");
            dos.writeBytes(HttpHeaderList.CONTENT_LENGHT.toString() + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HttpHeaderList.HTTP_1_1 + "200 OK \r\n");
            dos.writeBytes(HttpHeaderList.CONTENT_TYPE + "text/css;charset=utf-8\r\n");
            dos.writeBytes(HttpHeaderList.CONTENT_LENGHT.toString() + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HttpHeaderList.HTTP_1_1 + "302 Found \r\n");
            dos.writeBytes(HttpHeaderList.LOCATION + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HttpHeaderList.HTTP_1_1 + "302 Found \r\n");
            dos.writeBytes(HttpHeaderList.LOCATION + path + "\r\n");
            dos.writeBytes(HttpHeaderList.SET_COOKIE + "logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }



    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
