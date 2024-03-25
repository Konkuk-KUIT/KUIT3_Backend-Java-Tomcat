package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;
import webserver.httprequest.HttpRequest;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static webserver.httprequest.UrlPath.*;
import static webserver.httprequest.HttpMethod.*;

public class RequestHandler implements Runnable{
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
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);

            byte[] body = new byte[0];

            // 요구사항 1
            // / 이거나 index.html이면 바디에 해당 파일을 넘겨야함.
            if(httpRequest.getMethod().equals(GET.getMethod()) && httpRequest.getUrl().equals(INDEX.getPath())){
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + INDEX.getPath()));
            }

            if(httpRequest.getUrl().equals("/")){
                body = Files.readAllBytes(Paths.get("./webapp/index.html"));
            }

            // 요구사항 2
            //  SignUp 버튼을 클릭하면 /user/form.html 화면으로 이동
            if(httpRequest.getUrl().equals(USER_FORM.getPath())){
                body = Files.readAllBytes(Paths.get("./webapp" + USER_FORM.getPath()));
            }
            // /user/signup
            if(httpRequest.getMethod().equals(GET.getMethod()) &&httpRequest.getUrl().startsWith(USER_SIGNUP.getPath())){
                String tmp = httpRequest.getUrl().split("\\?")[1];
                Map<String,String> m = parseQueryParameter(tmp);
                User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
                repository.addUser(user);
                response302Header(dos,"/index.html");
                return;
            }

            // 요구사항 3
            if(httpRequest.getMethod().equals(POST.getMethod()) && httpRequest.getUrl().equals(USER_SIGNUP.getPath())){
                log.info("check");
                String queryString = IOUtils.readData(br, httpRequest.getContentLength());
                Map<String,String> m = parseQueryParameter(queryString);
                User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
                repository.addUser(user);
                response302Header(dos,INDEX.getPath());
                return;
            }


            // 요구사항 5
            if(httpRequest.getUrl().equals(USER_LOGIN_FILE.getPath())){
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + USER_LOGIN_FILE.getPath()));
            }

            if(httpRequest.getMethod().equals(POST.getMethod()) && httpRequest.getUrl().equals(USER_LOGIN.getPath())){
                String queryString = IOUtils.readData(br, httpRequest.getContentLength());
                Map<String,String> m = parseQueryParameter(queryString);
                User user = repository.findUserById(m.get("userId"));
                login(dos,m.get("password"),user);
            }

            // 요구사항 6 -> 문제점 현재 내 pc에서만의 문제인지 모르겠으나 쿠키 값이 여러개이고 ;으로 쿠키값이 끝난다
            if(httpRequest.getUrl().equals(USER_LIST.getPath())){
                if(!(httpRequest.getCookie().startsWith("logined=true"))){
                    response302Header(dos,"/user/login.html");
                }
                body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));

            }

            // 요구사항 7
            if (httpRequest.getUrl().endsWith(".css")) {
                body = Files.readAllBytes(Paths.get("./webapp" + httpRequest.getUrl()));
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

    private void login(DataOutputStream dos,String password, User user) {
        if (user != null && user.getPassword().equals(password)) {
            response302HeaderWithLogin(dos,INDEX.getPath());
            return;
        }
        response302Header(dos,LOGIN_FAILED.getPath());
    }

    // 요구사항 7
    private void response200HeaderWithCss(DataOutputStream dos,int lengthOfBodyContent) {
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

    // 요구사항 4
    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 요구사항 5
    private void response302HeaderWithLogin(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
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