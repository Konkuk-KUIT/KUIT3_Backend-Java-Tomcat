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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.getQueryParameter;

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
        repository = MemoryUserRepository.getInstance(); // 레포지토리 MemoryUserRepository로 초기화
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];

            String startLine = br.readLine();
            log.log(Level.INFO, "Received HTTP Request: " + startLine);

            String[] startLines = startLine.split(" ");
            String method = startLines[0]; // GET,POST,PUT,DELELTE,PATCH,GEAD, OPTIONS, CONNECT 읽어오기
            String url = startLines[1]; // Request target 해당 request가 전송되는 목표 uri

            int requestContentLength = 0; // http 요청 총 길이
            String cookie = "";

            while(true){
                final String line = br.readLine();
                if(line.equals("")){break;} // 다읽으면 while문 탈출
                // 헤더 정보
                if(line.startsWith("Content-Length")){
                    requestContentLength = Integer.parseInt(line.split(": ")[1]); // Content-Length : 101 이런식으로 있으니까 101만 잘라서 값으로 가져옴
                }
                if(line.startsWith("Cookie")){
                    cookie = line.split(": ")[1]; // 위와 같은 방법
                }
            }

            // 요구사항 1 - 기본화면 띄우기
            if(method.equals("GET") && url.endsWith(".html")){
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            // "/"경로로 요청을 보내는 것은 서버의 기본 페이지
            if(url.equals("/")){
                body=Files.readAllBytes(homePath);
            }

            // 요구사항 2 - 회원가입하기, 302상태코드 적용 - 제대로 안되고 있음
            if(method.equals("GET") && url.equals("/user/signup")){
                String queryString = IOUtils.readData(br,requestContentLength); // 쿼리스트링 읽어오기
                //log.log(Level.INFO, "Received Query String: " + queryString); // 쿼리스트링 출력
                Map<String, String> qParameter = getQueryParameter(queryString);
                User user = new User(qParameter.get("userId"), qParameter.get("password"), qParameter.get("name"), qParameter.get("email"));
                repository.addUser(user); // 신규회원 정보등록하기
                response302Header(dos,HOME_URL); // 기본화면으로 리다이렉트하기
                return;
            }

            // 요구사항 5 - 로그인하기
            if (url.equals("/user/login")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> qParameter = getQueryParameter(queryString);
                User user = repository.findUserById(qParameter.get("userId"));
                login(dos, qParameter, user);
                return;
            }

            // 요구 사항 6번 - 왜인지 모르겠는데 리스트창이 안뜸..자꾸 다시 기본화면으로 감
            if (url.equals("/user/userList")) {
                if (!cookie.equals("logined=true")) { //logined=true가 아니라면
                    response302Header(dos,LOGIN_URL); // 다시 로그인창 뜨게하기
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL + LIST_URL));
            }

            // 요구사항 7 - css 적용하기
            if(method.equals("GET")&&url.endsWith(".css")){
                body = Files.readAllBytes(Paths.get(ROOT_URL+url));
                response200HeaderWithCss(dos,body.length);
                responseBody(dos,body);
                return;
            }


            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void login(DataOutputStream dos, Map<String, String> queryParameter, User user) {
        if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
            response302HeaderWithCookie(dos,HOME_URL);
            return;
        }
        response302Header(dos,LOGIN_FAILED_URL);
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
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n"); // 상태코드 302번으로 바꾸기
            dos.writeBytes("Location: " + path + "\r\n"); // Location만 남김, body도 없음, 내가준 path가 url
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
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

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n"); // 헤더와 바디 구분
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}