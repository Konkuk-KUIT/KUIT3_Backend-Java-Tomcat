package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;

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
    private Controller controller = new ForwardController();    // 요구사항 1,7번 및 이미지

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // Header 분석
            HttpRequest httpRequest = HttpRequest.from(in);
            HttpResponse httpResponse = new HttpResponse(out);

            // 요구사항 1번
            if (httpRequest.getPath().equals("/")) {
                controller = new HomeController();
            }

            // 요구사항 2,3,4번
            if (httpRequest.getPath().equals("/user/signup")) {
                controller = new SignUpController();
            }

            // 요구사항 5번
            if (httpRequest.getMethod().equals("POST") && httpRequest.getPath().equals("/user/login")) {
                controller = new LoginController();
            }

            // 요구사항 6번
            if (httpRequest.getPath().equals("/user/userList")) {
               controller = new ListController();
            }
            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}