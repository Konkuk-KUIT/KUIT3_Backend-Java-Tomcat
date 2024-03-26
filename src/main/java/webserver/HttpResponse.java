package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static webserver.HttpHeader.*;
import static webserver.HttpStatus.OK;
import static webserver.HttpStatus.REDIRECT;


public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) {
        try {
            byte[] body = Files.readAllBytes(Path.of(path));
            if (path.endsWith(".css")) {
                response200HeaderWithCss(body.length);
            } else {
                response200Header(body.length);
            }
            responseBody(body);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    public void redirect(String path) {
        response302Header(path);
    }

    public void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes(OK.getStatusLine());
            dos.writeBytes(CONTENT_TYPE + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response200HeaderWithCss(int lengthOfBodyContent) {
        try {
            dos.writeBytes(OK.getStatusLine());
            dos.writeBytes(CONTENT_TYPE + ": text/css;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302Header(String path) {
        try {
            dos.writeBytes(REDIRECT.getStatusLine());
            dos.writeBytes(LOCATION + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302HeaderWithCookie(String path) {
        try {
            dos.writeBytes(REDIRECT.getStatusLine());
            dos.writeBytes(LOCATION + ": " + path + "\r\n");
            dos.writeBytes(SET_COOKIE + ": logined=true" + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
