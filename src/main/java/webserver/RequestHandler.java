package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.request.RequestURL;
import model.User;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(in);

            byte[] body = new byte[0];

            // 요구사항 1번
            if (httpRequest.getPath().equals("/")) {
                body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + RequestURL.HOME_URL.get()));
            }
            // url 에 맞는 웹페이지 반환
            if (httpRequest.getMethod().equals("GET") && httpRequest.getPath().endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + httpRequest.getPath()));
            }

            // 요구사항 2,3,4번
            if (httpRequest.getPath().equals("/user/signup")) {
                User user = new User(httpRequest.getQueryParameter("userId"),
                        httpRequest.getQueryParameter("password"),
                        httpRequest.getQueryParameter("name"),
                        httpRequest.getQueryParameter("email"));
                repository.addUser(user);
                // for redirect
                response302Header(dos, RequestURL.HOME_URL.get());
                return;
            }

            // 요구사항 5번
            if (httpRequest.getMethod().equals("POST") && httpRequest.getPath().equals("/user/login")) {
                User user = repository.findUserById(httpRequest.getQueryParameter("userId"));

                // 로그인 성공
                if (user != null && user.getPassword().equals(httpRequest.getQueryParameter("password"))) {
                    response302HeaderWithCookie(dos, RequestURL.HOME_URL.get());
                    return;
                }
                // 로그인 실패
                response302Header(dos, RequestURL.LOGIN_FAILED_URL.get());
                return;
            }

            // 요구사항 6번
            if (httpRequest.getPath().equals("/user/userList")) {
                // 비로그인 상태 : redirect to /user/login.html
                String cookie = httpRequest.getField("Cookie");
                log.info(cookie);
                if (cookie == null || !cookie.equals("logined=true")) {
                    response302Header(dos, RequestURL.LOGIN_URL.get());
                    return;
                }
                // 로그인 상태 : user/list.html 반환
                body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + RequestURL.LIST_URL.get()));
            }

            // 요구사항 7번
            if (httpRequest.getMethod().equals("GET") && httpRequest.getPath().endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + httpRequest.getPath()));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // 이미지
            if (httpRequest.getMethod().equals("GET") && httpRequest.getPath().endsWith(".jpeg")) {
                body = Files.readAllBytes(Paths.get(RequestURL.ROOT_URL.get() + httpRequest.getPath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if (body.length == 0) {
                body = "Sorry, This page doesn't exist.".getBytes();
            }
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
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

    // for redirect
    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 트러블 슈팅 (Cookie Path)
    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}