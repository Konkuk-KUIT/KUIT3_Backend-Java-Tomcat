package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
            HttpRequest httpRequest = HttpRequest.from(in);
            HttpResponse httpResponse = new HttpResponse(out);

            // 요구사항 1번
            if (httpRequest.getPath().equals("/")) {
                httpResponse.forward(RequestURL.HOME_URL.get());
                return;
            }
            // url 에 맞는 웹페이지 반환
            if (httpRequest.getMethod().equals("GET") && httpRequest.getPath().endsWith(".html")) {
                httpResponse.forward(httpRequest.getPath());
                return;
            }

            // 요구사항 2,3,4번
            if (httpRequest.getPath().equals("/user/signup")) {
                User user = new User(httpRequest.getQueryParameter("userId"),
                        httpRequest.getQueryParameter("password"),
                        httpRequest.getQueryParameter("name"),
                        httpRequest.getQueryParameter("email"));
                repository.addUser(user);
                httpResponse.redirect(RequestURL.HOME_URL.get());
                return;
            }

            // 요구사항 5번
            if (httpRequest.getMethod().equals("POST") && httpRequest.getPath().equals("/user/login")) {
                User user = repository.findUserById(httpRequest.getQueryParameter("userId"));

                // 로그인 성공
                if (user != null && user.getPassword().equals(httpRequest.getQueryParameter("password"))) {
                    httpResponse.redirectWithCookie(RequestURL.HOME_URL.get());
                    return;
                }
                // 로그인 실패
                httpResponse.redirect(RequestURL.LOGIN_FAILED_URL.get());
                return;
            }

            // 요구사항 6번
            if (httpRequest.getPath().equals("/user/userList")) {
                // 비로그인 상태 : redirect to /user/login.html
                String cookie = httpRequest.getField("Cookie");
                if (cookie == null || !cookie.equals("logined=true")) {
                    httpResponse.redirect(RequestURL.LOGIN_URL.get());
                    return;
                }
                // 로그인 상태 : user/list.html 반환
                httpResponse.forward(RequestURL.LIST_URL.get());
                return;
            }

            // 요구사항 7번
            if (httpRequest.getMethod().equals("GET") && httpRequest.getPath().endsWith(".css")) {
                httpResponse.forward(httpRequest.getPath());
                return;
            }

            // 이미지
            if (httpRequest.getMethod().equals("GET") && httpRequest.getPath().endsWith(".jpeg")) {
                httpResponse.forward(httpRequest.getPath());
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}