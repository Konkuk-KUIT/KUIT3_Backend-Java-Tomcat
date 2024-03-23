package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static http.util.HttpRequestUtils.parseQueryParameter;

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

            // input stream 확인
            // ex)
            // GET /index.html HTTP/1.1 이런 형식으로 올거임
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            byte[] body = new byte[0];

            int requestContentLength = 0;
            String cookie = "";


            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if(line.startsWith("Cookie")){
                    cookie = line.split(": ")[1];
                    cookie = cookie.split(" ")[0];
                }
            }

            // 요구사항 1
            // / 이거나 index.html이면 바디에 해당 파일을 넘겨야함.
            if(method.equals("GET") && url.equals("/index.html")){
                body = Files.readAllBytes(Paths.get("./webapp" + url));
            }

            if(url.equals("/")){
                body = Files.readAllBytes(Paths.get("./webapp/index.html"));
            }

            // 요구사항 2
            //  SignUp 버튼을 클릭하면 /user/form.html 화면으로 이동
            if(method.equals("GET") && url.equals("/user/form.html")){
                body = Files.readAllBytes(Paths.get("./webapp" + url));
            }
            // /user/signup
            if(method.equals("GET") && url.startsWith("/user/signup")){
                String tmp = url.split("\\?")[1];
                Map<String,String> m = parseQueryParameter(tmp);
                User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
                repository.addUser(user);
                response302Header(dos,"/index.html");
                return;
            }

            // 요구사항 3
            if(method.equals("POST") && url.equals("/user/signup")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String,String> m = parseQueryParameter(queryString);
                User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
                repository.addUser(user);
                response302Header(dos,"/index.html");
                return;
            }


            // 요구사항 5
            if(method.equals("GET") && url.equals("/user/login.html")){
                body = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
            }

            if(method.equals("POST") && url.equals("/user/login")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String,String> m = parseQueryParameter(queryString);
                User user = repository.findUserById(m.get("userId"));
                login(dos,m.get("password"),user);
            }

            // 요구사항 6 -> 문제점 현재 내 pc에서만의 문제인지 모르겠으나 쿠키 값이 여러개이고 ;으로 쿠키값이 끝난다
            if(method.equals("GET") && url.equals("/user/userList")){
                if(!(cookie.startsWith("logined=true"))){
                    log.info(cookie);
                    response302Header(dos,"/user/login.html");
                }
                body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));

            }

            // 요구사항 7
            if (method.equals("GET") && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get("./webapp" + url));
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
            response302HeaderWithLogin(dos,"/index.html");
            return;
        }
        response302Header(dos,"/login_failed.html");
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