package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    private static final String LOGIN_URL = "/user/login.html";
    private static final String LIST_URL = "/user/list.html";

    private final Repository repository;
    private final Path homePath = Paths.get(ROOT_URL + HOME_URL);

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

//            byte[] body = "Hello World".getBytes();
            byte[] body = new byte[0];

//             InputStream의 요청을 읽어와 HTTP message의 startLine을 분석하는 코드
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];
            log.info("Received HTTP request: " + method + " " + url);

            int requestContentLength = 0;
            String cookie = "";

            while (true) {
                final String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                }
            }

            // body 저장
            if(method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            if(url.equals("/")) {
                body = Files.readAllBytes(homePath);
            }

            /**
             * 회원가입하기
             * GET 방식 : URL 뒤에 파라미터를 추가하여 데이터 전달
             * POST 방식 : HTTP 요청 본문(body)에 데이터를 추가하여 전달
             */

            if(url.equals("/user/signup")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);
                response302Header(dos, HOME_URL);
                return;
            }

            // 로그인하기
            if(url.equals("/user/login")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = repository.findUserById(queryParameter.get("userId"));
                login(dos, queryParameter, user);
                return;
            }

            // 사용자 목록 출력
            if(url.equals("/user/userList")) {
                if(!cookie.equals("logined=true")) {
                    response302Header(dos, LOGIN_URL);
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL + LIST_URL));
            }

            // CSS 출력
            if(method.equals("GET") && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }


            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void login (DataOutputStream dos, Map<String, String> queryParameter, User user) {
        if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
            response302HeaderWithCookie(dos, HOME_URL);
            return;
        }
        response302Header(dos, LOGIN_FAILED_URL);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            log.info("sent HTTP response : content Length: " + lengthOfBodyContent);
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

    private void response302Header(DataOutputStream dos, String path) {
        // 302 일시 리다이렉트 : 말그대로 요청한 정보(사이트나 페이지)가 일시(임시)적으로 옮겼다는 것
        try{
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        }catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
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

}