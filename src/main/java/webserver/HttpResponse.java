package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static webserver.enums.HttpHeader.*;
import static webserver.enums.HttpStatus.OK;
import static webserver.enums.HttpStatus.REDIRECT;


public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;

    byte[] body = new byte[0];

    public HttpResponse(DataOutputStream dos) throws IOException {
        this.dos = dos;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void forward(String path) {
        try {
            body = Files.readAllBytes(Path.of(path));
            if (path.endsWith(".css") || path.endsWith(".jpg")) {
                response200HeaderWithCss();
            } else {
                response200Header();
            }
            responseBody();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    public void redirect(String path) {
        response302Header(path);
    }

    public void response200Header() {
        try {
            dos.writeBytes(OK.getStatusLine());
            dos.writeBytes(CONTENT_TYPE.getHeader() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader() + ": " + body.length + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response200HeaderWithCss() {
        try {
            dos.writeBytes(OK.getStatusLine());
            dos.writeBytes(CONTENT_TYPE.getHeader() + ": text/css;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader() + ": " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302Header(String path) {
        try {
            dos.writeBytes(REDIRECT.getStatusLine());
            dos.writeBytes(LOCATION.getHeader() + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302HeaderWithCookie(String path) {
        try {
            dos.writeBytes(REDIRECT.getStatusLine());
            dos.writeBytes(LOCATION.getHeader() + ": " + path + "\r\n");
            dos.writeBytes(SET_COOKIE.getHeader() + ": logined=true" + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void responseBody() {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
