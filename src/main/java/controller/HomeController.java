package controller;

import controller.Controller;
import http.HttpRequest;
import http.HttpResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import structure.StartLine;

public class HomeController implements Controller {     // Get에만 응답하는 친구 입니다.

    @Override
    public HttpResponse runLogic(HttpRequest httpRequest) {
        byte[] byteFile = readFile("/Users/tony/IdeaProjects/KUIT3_Backend-Java-Tomcat/webapp/index.html");
        return getHttpResponseHeader(byteFile);
    }

    private HttpResponse getHttpResponseHeader(byte[] byteFile) {
        String header = "HTTP/1.1 200 OK \r\n" + "Content-Type: text/html;charset=utf-8\r\n" + "Content-Length: " +
                byteFile.length + "\r\n" + "\r\n";
        byte[] headerByte = header.getBytes();
        return new HttpResponse(headerByte, byteFile);
    }

    private byte[] readFile(String path) {
        try (FileInputStream input = new FileInputStream(path)) {    // TODO: 얘 고쳐라, Path.of() 몰라
            return input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
