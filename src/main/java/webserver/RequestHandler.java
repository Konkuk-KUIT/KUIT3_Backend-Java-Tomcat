package webserver;

import HttpRequest.HttpRequest;
import db.MemoryUserRepository;
import HttpResponse.ResponseHeaderConstants;
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
            MemoryUserRepository db_user = getInstance();

            // br로 읽어들인 request 분석
            HttpRequest httpRequest = HttpRequest.from(br);

            // request 분석 결과 필요한 정보들
            String requestMethod = httpRequest.getMethod();
            String requestUrl = httpRequest.getUrl();
            int requestContentLength = httpRequest.getContentLength();
            String cookies = httpRequest.getCookie();
            String requestBody = httpRequest.getBody();

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
                db_user.addUser(user);

                Collection<User> allUsers = db_user.findAll();
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
                db_user.addUser(user);

                Collection<User> allUsers = db_user.findAll();
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
                System.out.println(requestBody);
                Map<String, String> queryStringMap = parseQueryParameter(requestBody);

                User user = db_user.findUserById(queryStringMap.get("userId"));
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
                    String[] cookieValues = cookieParts[1].split(";");
                    boolean isLogined = false;
                    // 쿠키 값 확인
                    for (String cookie : cookieValues) {
                        if (cookie.trim().equals("logined=true")) {
                            isLogined = true;
                            break;
                        }
                    }
                    if (isLogined) {
                        System.out.println("로그인한 사용자인 경우");
                        response200Header(dos, body.length);
                    } else {
                        System.out.println("쿠키는 있지만 로그인하지 않은 경우");
                        response302Header(dos, "/user/login.html");
                    }
                }
                else {
                    System.out.println("쿠키 없음");
                    response302Header(dos, "/user/login.html");
                }
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
            dos.writeBytes(ResponseHeaderConstants.START_LINE_200.getValue());
            dos.writeBytes(ResponseHeaderConstants.CONTENT_TYPE_HTML.getValue());
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos) {
        try {
            dos.writeBytes(ResponseHeaderConstants.START_LINE_200.getValue());
            dos.writeBytes(ResponseHeaderConstants.CONTENT_TYPE_CSS.getValue());
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(ResponseHeaderConstants.START_LINE_302.getValue());
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path, String cookie) {
        try {
            dos.writeBytes(ResponseHeaderConstants.START_LINE_302.getValue());
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