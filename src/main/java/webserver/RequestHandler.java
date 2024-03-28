package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String BASE_URL = "./webapp";
    private static final String HOME_URL = "/index.html";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String homeUrl = BASE_URL + HOME_URL;
            byte[] body = new byte[0];
            int requestContentLength = 0;
            String cookies = "";
            MemoryUserRepository userRepository = getInstance();

            // InputStream에서 요청을 읽어와 StartLine 파싱
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String requestMethod = startLines[0];
            String requestUrl = startLines[1];

            // Header 파싱
            while (true) {
                final String line = br.readLine();
                // blank line 만나면 requestBody 시작되므로 break
                if (line.equals("")) {
                    break;
                }
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    cookies = line;
                }
            }

            // 1) 기본값(홈) url 설정
            if (requestMethod.equals("GET") && requestUrl.equals("/")) {
                body = Files.readAllBytes(Paths.get(homeUrl));
            }

            // 1) .html로 끝나는 url의 경우
            if (requestMethod.equals("GET") && requestUrl.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(BASE_URL + requestUrl));
            }

            // 2) GET 방식으로 회원가입
            if (requestMethod.equals("GET") && requestUrl.startsWith("/user/signup?")) {
                // 쿼리 스트링 기준으로 파싱
                String[] parsedUrl = requestUrl.split("[?]");
                Map<String, String> queryStringMap = parseQueryParameter(parsedUrl[1]);

                // URL 인코딩된 값을 디코딩
                for (Map.Entry<String, String> entry : queryStringMap.entrySet()){
                    try {
                        String key = entry.getKey();
                        // 디코딩
                        String decodedValue = java.net.URLDecoder.decode(entry.getValue(),"UTF-8");
                        queryStringMap.put(key, decodedValue);
                    } catch (java.io.UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                String userId = queryStringMap.get("userId");
                String name = queryStringMap.get("name");
                String password = queryStringMap.get("password");
                String email = queryStringMap.get("email");

                // MemoryUserRepository 객체에 User 저장 및 확인
                User user = new User(userId, password, name, email);
                userRepository.addUser(user);

                Collection<User> allUsers = userRepository.findAll();
                for (User storedUser: allUsers) {
                    System.out.println("userId: " + storedUser.getUserId());
                    System.out.println("name: " + storedUser.getName());
                    System.out.println("password: " + storedUser.getPassword());
                    System.out.println("email: " + storedUser.getEmail());
                    System.out.println();
                }

                response302Header(dos, "/");
            }

            // 3) POST 방식으로 회원가입 + 4) 302 status code 적용
            if (requestMethod.equals("POST") && requestUrl.equals("/user/signup")) {
                String requestBody = readData(br, requestContentLength);
                Map<String, String> queryStringMap = parseQueryParameter(requestBody);

                // 2번이랑 동일 -> 리팩토링할 때 함수로 빼기
                // URL 인코딩된 값을 디코딩
                for (Map.Entry<String, String> entry : queryStringMap.entrySet()){
                    try {
                        String key = entry.getKey();
                        // 디코딩
                        String decodedValue = java.net.URLDecoder.decode(entry.getValue(),"UTF-8");
                        queryStringMap.put(key, decodedValue);
                    } catch (java.io.UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                String userId = queryStringMap.get("userId");
                String name = queryStringMap.get("name");
                String password = queryStringMap.get("password");
                String email = queryStringMap.get("email");

                // MemoryUserRepository 객체에 User 저장 및 확인
                User user = new User(userId, password, name, email);
                userRepository.addUser(user);

                Collection<User> allUsers = userRepository.findAll();
                for (User storedUser: allUsers) {
                    System.out.println("userId: " + storedUser.getUserId());
                    System.out.println("name: " + storedUser.getName());
                    System.out.println("password: " + storedUser.getPassword());
                    System.out.println("email: " + storedUser.getEmail());
                    System.out.println();
                }

                response302Header(dos, "/");
            }

            // 5) 로그인
            if (requestMethod.equals("POST") && requestUrl.equals("/user/login")) {
                String requestBody = readData(br, requestContentLength);
                Map<String, String> queryStringMap = parseQueryParameter(requestBody);

                User user = userRepository.findUserById(queryStringMap.get("userId"));
                // 회원인 경우
                if (user != null) {
                    String cookie = "logined=true";
                    response302HeaderWithCookie(dos, "/", cookie);
                } else { // 비회원인 경우
                    response302Header(dos, "/user/login_failed.html");
                }
            }

            // 6) 사용자 목록 출력
            if (requestMethod.equals("GET") && requestUrl.equals("/user/list.html")) {
                // 리스폰스 헤더로부터 쿠키 가져와서 -> logined=true일 때에만 user list 화면으로 / 로그인 아니면 login.html으로 redirect
                if(!cookies.isEmpty()) {
                    String[] cookieParts = cookies.split(":");
                    if (cookieParts.length >= 2) {
                        String[] cookieValue = cookieParts[1].split(";");
                        // 쿠키 값 확인
                        for (String cookie : cookieValue) {
                            if (cookie.trim().equals("logined=true")) {
                                System.out.println("헤더에 logined=true 쿠키 있는 경우");
                                response302Header(dos, "/user/list.html");
                                return;
                            }
                        }
                    }
                }
                System.out.println("헤더에 쿠키 없는 경우");
                response302Header(dos, "/user/login.html");
            }

            // 7) .css로 끝나는 url의 경우
            if (requestUrl.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(BASE_URL + requestUrl));
                response200HeaderWithCss(dos);
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
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

    private void response200HeaderWithCss(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "; Path=" + path + "\r\n");
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



}