package webserver;

import com.sun.security.jgss.GSSUtil;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGINED_FAILED_URL = "/user/login_failed.html";
    private final Repository repository;
    private final Path homePath = Paths.get(ROOT_URL + HOME_URL);

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.repository = MemoryUserRepository.getInstance()
        ;
    } //클라이언트와 연결을 받아들이 소켓을 인스턴스 변수에 할당

    @Override
    public void run() { // 클라이언트 요청 처리
        // 클라이언트 연결 정보 기록
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress()
                + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];

            String requestLine = br.readLine();
            String[] requestLines = requestLine.split(" ");
            String method = requestLines[0];
            String url = requestLines[1];

            int requestContentLength = 0;

            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }

            if (method.equals("GET") && url.equals("/")) {
                body = Files.readAllBytes(homePath);
            }

            if (method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            if (url.equals("/user/signup")) {
                // readBody 메소드에 contentLength, BufferedReader를 함께 보내어 body 값을 읽을 수 있을 것
                String queryString = IOUtils.readData(br, requestContentLength);
                // 새로운 user 인스턴스 생성, MemoryUserRepository에 저장
                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(queryString);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"),
                        queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);

                response302Header(dos, HOME_URL);
                return;
            }

            if (url.equals("/user/login")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(queryString);
                User user = repository.findUserById(queryParameter.get("userId"));
                login(dos, queryParameter, user);
                return;
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
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n"); // 상태코드 변경
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n"); // 상태코드 변경
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404NotFound(DataOutputStream dos) {
        try {
            String notFoundMessage = "<html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1></body></html>";
            byte[] notFoundBody = notFoundMessage.getBytes("UTF-8");
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + notFoundBody.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(notFoundBody, 0, notFoundBody.length);
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

    private void login(DataOutputStream dos, Map<String, String> queryParameter, User user) {
        if (user != null && user.getPassword().equals((queryParameter.get("password")))) {
            response302HeaderWithCookie(dos, HOME_URL);
            return;
        }
        response302Header(dos, LOGINED_FAILED_URL);
    }

}