package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    private static final String LOGIN_URL = "/user/login.html";
    private static final String LIST_URL = "/user/list.html";

    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];

            // request message start-line 검증
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            // Content-Length 랑 Cookie 가져오고, BufferedReader offset 을 request message (http) body 입구에 위치
            int requestContentLength = 0;
            String cookie = "";
            String line;
            while (!(line = br.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                }
            }

            // 요구사항 1번
            if (url.equals("/")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + HOME_URL));
            }
            // url 에 맞는 웹페이지 반환
            if (method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            // 요구사항 2,3,4번
            if (url.equals("/user/signup")) {
                String queryString = makeQueryStringByMethod(method, br, requestContentLength, url);
                Map<String, String> elements = HttpRequestUtils.parseQueryParameter(queryString);
                repository.addUser(new User(elements.get("userId"), elements.get("password"), elements.get("name"), elements.get("email")));
                // for redirect
                response302Header(dos, HOME_URL);
                return;
            }

            // 요구사항 5번
            if (method.equals("POST") && url.equals("/user/login")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> elements = HttpRequestUtils.parseQueryParameter(queryString);
                User user = repository.findUserById(elements.get("userId"));

                // 로그인 성공
                if (user != null && user.getPassword().equals(elements.get("password"))) {
                    response302HeaderWithCookie(dos, HOME_URL);
                    return;
                }
                // 로그인 실패
                response302Header(dos, LOGIN_FAILED_URL);
                return;
            }

            // 요구사항 6번
            if (url.equals("/user/userList")) {
                // 비로그인 상태 : redirect to /user/login.html
                if (!cookie.equals("logined=true")) {
                    response302Header(dos, LOGIN_URL);
                    return;
                }
                // 로그인 상태 : user/list.html 반환
                body = Files.readAllBytes(Paths.get(ROOT_URL + LIST_URL));
            }

            // 요구사항 7번
            if (method.equals("GET") && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
                response200HeaderWithCss(dos, body.length);
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

    private String makeQueryStringByMethod(String method, BufferedReader br, int requestContentLength, String url) throws IOException {
        if (method.equals("POST")) {
            return IOUtils.readData(br, requestContentLength);
        }
        // GET 방식
        int index = url.indexOf("?");
        return url.substring(index + 1);
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