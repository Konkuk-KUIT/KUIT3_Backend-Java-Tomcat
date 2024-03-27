package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // request message start-line 검증
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            // Content-Length 랑 Cookie 가져오고, BufferedReader offset 을 request message (http) body 입구에 위치
            int requestContentLength = 0;
            String cookie = "";
            String line = "";
            while (!(line = br.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                }
            }

            if (url.equals("/user/userList")) {
                if (cookie.equals("logined=true")) {
                    // user/list.html 반환
                    byte[] body = Files.readAllBytes(new File("webapp/user/list.html").toPath());
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                    return;
                }
                // 비로그인 상태
                response302Header(dos, "/user/login.html");
                return;
            }
            if (url.equals("/") || url.equals("/index.html")) {
                // index.html 반환
                byte[] body = Files.readAllBytes(new File("webapp/index.html").toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }
            if (url.equals("/user/form.html")) {
                // user/form.html 반환
                byte[] body = Files.readAllBytes(new File("webapp/user/form.html").toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }
            if (url.startsWith("/user/signup") && method.equals("GET")) {
                int index = url.indexOf("?");
                String queryString = url.substring(index + 1);
                Map<String, String> elements = HttpRequestUtils.parseQueryParameter(queryString);
                MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
                userRepository.addUser(new User(elements.get("userId"), elements.get("password"), elements.get("name"), elements.get("email")));
                // for redirect
                response302Header(dos, "/index.html");
                return;
            }
            if (url.startsWith("/user/signup") && method.equals("POST")) {
                Map<String, String> elements = HttpRequestUtils.parseQueryParameter(IOUtils.readData(br, requestContentLength));
                MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
                userRepository.addUser(new User(elements.get("userId"), elements.get("password"), elements.get("name"), elements.get("email")));
                // for redirect
                response302Header(dos, "/index.html");
                return;
            }
            if (url.equals("/user/login.html")) {
                // user/login.html 반환
                byte[] body = Files.readAllBytes(new File("webapp/user/login.html").toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }
            if (url.equals("/user/login_failed.html")) {
                // user/login_failed.html 반환
                byte[] body = Files.readAllBytes(new File("webapp/user/login_failed.html").toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }
            if (url.startsWith("/user/login") && method.equals("POST")) {
                Map<String, String> elements = HttpRequestUtils.parseQueryParameter(IOUtils.readData(br, requestContentLength));
                MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
                try {
                    User user = userRepository.findUserById(elements.get("userId"));
                    // 비밀번호가 틀린 경우
                    if (!user.getPassword().equals(elements.get("password"))) {
                        response302Header(dos, "/user/login_failed.html");
                        return;
                    }
                    response302HeaderWithCookie(dos, "/index.html");
                } catch (NullPointerException e) {
                    // 해당 아이디가 없는 경우
                    response302Header(dos, "/user/login_failed.html");
                }
            }
            if (url.endsWith(".css")) {
                byte[] body = Files.readAllBytes(new File("webapp/css/styles.css").toPath());
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
            }

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

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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

    // for redirect
    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
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
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}