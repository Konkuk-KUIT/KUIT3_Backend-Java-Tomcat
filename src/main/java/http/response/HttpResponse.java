package http.response;


import http.request.HttpHeader;
import http.request.RequestURL;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpResponse {
    private final DataOutputStream dos;
    private HttpResponseStatusLine statusLine;
    private HttpHeader httpHeader;
    private byte[] body;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
        this.httpHeader = new HttpHeader(new HashMap<>());
        this.body = new byte[0];
    }

    public void forward(String path) throws IOException {
        statusLine = new HttpResponseStatusLine("HTTP/1.1", "200", "OK");
        if (path.endsWith(".html")) {
            httpHeader.put("Content-Type", "text/html;charset=utf-8");
        }
        if (path.endsWith(".css")) {
            httpHeader.put("Content-Type", "text/css");
        }
        if (path.endsWith(".jpeg")) {
            httpHeader.put("Content-Type", "image/jpeg");
        }
        setBody(path);
        write();
    }

    private void setBody(String path) throws IOException {
        if (path.equals("/")) {
            body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + RequestURL.HOME_URL.get()));
        }
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".jpeg")) {
            body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + path));
        }
        if (path.equals("/user/userList")) {
            body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + RequestURL.LIST_URL));
        }
        if (body.length == 0) {
            body = "Sorry, This page doesn't exist.".getBytes();
        }
        httpHeader.put("Content-Length", String.valueOf(body.length));
    }

    public void redirect(String path) throws IOException {
        statusLine = new HttpResponseStatusLine("HTTP/1.1", "302", "Found");
        httpHeader.put("Location", path);
        write();
    }

    public void redirectWithCookie(String path) throws IOException {
        statusLine = new HttpResponseStatusLine("HTTP/1.1", "302", "Found");
        httpHeader.put("Location", path);
        httpHeader.put("Set-Cookie", "logined=true; Path=/");
        write();
    }

    private void write() throws IOException {
        dos.writeBytes(statusLine.getVersion() + " " + statusLine.getStatusCode() + " " + statusLine.getStatusMessage() + " \r\n");
        dos.writeBytes(httpHeader.toString());
        dos.write(body);
        dos.flush();
    }
}
